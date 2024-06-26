package net.latinplay.latinbot.jda.api;

import com.neovisionaries.ws.client.WebSocketFactory;
import net.latinplay.latinbot.jda.annotations.Incubating;
import net.latinplay.latinbot.jda.api.audio.factory.DefaultSendFactory;
import net.latinplay.latinbot.jda.api.audio.factory.IAudioSendFactory;
import net.latinplay.latinbot.jda.api.audio.factory.IAudioSendSystem;
import net.latinplay.latinbot.jda.api.entities.Activity;
import net.latinplay.latinbot.jda.api.events.RawGatewayEvent;
import net.latinplay.latinbot.jda.api.events.ReadyEvent;
import net.latinplay.latinbot.jda.api.events.message.MessageBulkDeleteEvent;
import net.latinplay.latinbot.jda.api.exceptions.AccountTypeException;
import net.latinplay.latinbot.jda.api.hooks.EventListener;
import net.latinplay.latinbot.jda.api.hooks.*;
import net.latinplay.latinbot.jda.api.managers.Presence;
import net.latinplay.latinbot.jda.api.requests.RestAction;
import net.latinplay.latinbot.jda.api.sharding.ShardManager;
import net.latinplay.latinbot.jda.api.utils.ChunkingFilter;
import net.latinplay.latinbot.jda.api.utils.Compression;
import net.latinplay.latinbot.jda.api.utils.SessionController;
import net.latinplay.latinbot.jda.api.utils.SessionControllerAdapter;
import net.latinplay.latinbot.jda.api.utils.cache.CacheFlag;
import net.latinplay.latinbot.jda.internal.JDAImpl;
import net.latinplay.latinbot.jda.internal.managers.PresenceImpl;
import net.latinplay.latinbot.jda.internal.utils.Checks;
import net.latinplay.latinbot.jda.internal.utils.config.AuthorizationConfig;
import net.latinplay.latinbot.jda.internal.utils.config.MetaConfig;
import net.latinplay.latinbot.jda.internal.utils.config.SessionConfig;
import net.latinplay.latinbot.jda.internal.utils.config.ThreadingConfig;
import net.latinplay.latinbot.jda.internal.utils.config.flags.ConfigFlag;
import okhttp3.OkHttpClient;

import javax.security.auth.login.LoginException;
import java.util.*;
import java.util.concurrent.*;

public class JDABuilder
{
    protected final List<Object> listeners;
    protected final AccountType accountType;

    protected ScheduledExecutorService rateLimitPool = null;
    protected boolean shutdownRateLimitPool = true;
    protected ScheduledExecutorService mainWsPool = null;
    protected boolean shutdownMainWsPool = true;
    protected ExecutorService callbackPool = null;
    protected boolean shutdownCallbackPool = true;
    protected EnumSet<CacheFlag> cacheFlags = EnumSet.allOf(CacheFlag.class);
    protected ConcurrentMap<String, String> contextMap = null;
    protected SessionController controller = null;
    protected VoiceDispatchInterceptor voiceDispatchInterceptor = null;
    protected OkHttpClient.Builder httpClientBuilder = null;
    protected OkHttpClient httpClient = null;
    protected WebSocketFactory wsFactory = null;
    protected String token = null;
    protected IEventManager eventManager = null;
    protected IAudioSendFactory audioSendFactory = null;
    protected JDA.ShardInfo shardInfo = null;
    protected Compression compression = Compression.ZLIB;
    protected Activity activity = null;
    protected OnlineStatus status = OnlineStatus.ONLINE;
    protected boolean idle = false;
    protected int maxReconnectDelay = 900;
    protected int largeThreshold = 250;
    protected int maxBufferSize = 2048;
    protected EnumSet<ConfigFlag> flags = ConfigFlag.getDefault();
    protected ChunkingFilter chunkingFilter = ChunkingFilter.ALL;

    /**
     * Creates a completely empty JDABuilder.
     *
     * <br>If you use this, you need to set the token using
     * {@link JDABuilder#setToken(String) setToken(String)}
     * before calling {@link JDABuilder#build() build()}
     *
     * @see #JDABuilder(String)
     */
    public JDABuilder()
    {
        this(AccountType.BOT);
    }

    /**
     * Creates a JDABuilder with the predefined token.
     *
     * @param token
     *        The bot token to use
     *
     * @see   #setToken(String)
     */
    public JDABuilder(String token)
    {
        this();
        setToken(token);
    }

    /**
     * Creates a completely empty JDABuilder.
     * <br>If you use this, you need to set the token using
     * {@link JDABuilder#setToken(String) setToken(String)}
     * before calling {@link JDABuilder#build() build()}
     *
     * @param  accountType
     *         The {@link AccountType AccountType}.
     *
     * @throws IllegalArgumentException
     *         If the given AccountType is {@code null}
     *
     * @incubating Due to policy changes for the discord API this method may not be provided in a future version
     */
    @Incubating
    public JDABuilder(AccountType accountType)
    {
        Checks.notNull(accountType, "accountType");

        this.accountType = accountType;
        this.listeners = new LinkedList<Object>();
    }

    /**
     * Whether JDA should fire {@link RawGatewayEvent} for every discord event.
     * <br>Default: {@code false}
     *
     * @param  enable
     *         True, if JDA should fire {@link RawGatewayEvent}.
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @since  4.0.0
     */
    public JDABuilder setRawEventsEnabled(boolean enable)
    {
        return setFlag(ConfigFlag.RAW_EVENTS, enable);
    }

    /**
     * Whether the rate-limit should be relative to the current time plus latency.
     * <br>By default we use the {@code X-RateLimit-Reset-After} header to determine when
     * a rate-limit is no longer imminent. This has the disadvantage that it might wait longer than needed due
     * to the latency which is ignored by the reset-after relative delay.
     *
     * <p>When disabled, we will use the {@code X-RateLimit-Reset} absolute timestamp instead which accounts for
     * latency but requires a properly NTP synchronized clock to be present.
     * If your system does have this feature you might gain a little quicker rate-limit handling than the default allows.
     *
     * <p>Default: <b>true</b>
     *
     * @param  enable
     *         True, if the relative {@code X-RateLimit-Reset-After} header should be used.
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @since  4.1.0
     */
    public JDABuilder setRelativeRateLimit(boolean enable)
    {
        return setFlag(ConfigFlag.USE_RELATIVE_RATELIMIT, enable);
    }

    /**
     * Flags used to enable selective parts of the JDA cache to reduce the runtime memory footprint.
     * <br><b>It is highly recommended to use {@link #setDisabledCacheFlags(EnumSet)} instead
     * for backwards compatibility</b>. We might add more flags in the future which you then effectively disable
     * when updating and not changing your setting here.
     *
     * @param  flags
     *         EnumSet containing the flags for cache services that should be <b>enabled</b>
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @see    #setDisabledCacheFlags(EnumSet)
     */
    public JDABuilder setEnabledCacheFlags(EnumSet<CacheFlag> flags)
    {
        this.cacheFlags = flags == null ? EnumSet.noneOf(CacheFlag.class) : EnumSet.copyOf(flags);
        return this;
    }

    /**
     * Flags used to disable parts of the JDA cache to reduce the runtime memory footprint.
     * <br>Shortcut for {@code setEnabledCacheFlags(EnumSet.complementOf(flags))}
     *
     * @param  flags
     *         EnumSet containing the flags for cache services that should be <b>disabled</b>
     *
     * @return The JDABuilder instance. Useful for chaining.
     */
    public JDABuilder setDisabledCacheFlags(EnumSet<CacheFlag> flags)
    {
        return setEnabledCacheFlags(flags == null ? EnumSet.allOf(CacheFlag.class) : EnumSet.complementOf(flags));
    }

    public JDABuilder setContextMap(ConcurrentMap<String, String> map)
    {
        this.contextMap = map;
        if (map != null)
            setContextEnabled(true);
        return this;
    }

    /**
     * Whether JDA should use a synchronized MDC context for all of its controlled threads.
     * <br>Default: {@code true}
     *
     * @param  enable
     *         True, if JDA should provide an MDC context map
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @see    <a href="https://www.slf4j.org/api/org/slf4j/MDC.html" target="_blank">MDC Javadoc</a>
     * @see    #setContextMap(java.util.concurrent.ConcurrentMap)
     */
    public JDABuilder setContextEnabled(boolean enable)
    {
        return setFlag(ConfigFlag.MDC_CONTEXT, enable);
    }

    /**
     * Sets the compression algorithm used with the gateway connection,
     * this will decrease the amount of used bandwidth for the running bot instance
     * for the cost of a few extra cycles for decompression.
     * Compression can be entirely disabled by setting this to {@link Compression#NONE}.
     * <br><b>Default: {@link Compression#ZLIB}</b>
     *
     * <p><b>We recommend to keep this on the default unless you have issues with the decompression.</b>
     * <br>This mode might become obligatory in a future version, do not rely on this switch to stay.
     *
     * @param  compression
     *         The compression algorithm to use with the gateway connection
     *
     * @throws java.lang.IllegalArgumentException
     *         If provided with null
     *
     * @return The JDABuilder instance. Useful for chaining
     *
     * @see    <a href="https://discordapp.com/developers/docs/topics/gateway#transport-compression" target="_blank">Official Discord Documentation - Transport Compression</a>
     */
    public JDABuilder setCompression(Compression compression)
    {
        Checks.notNull(compression, "Compression");
        this.compression = compression;
        return this;
    }

    /**
     * Whether the Requester should retry when
     * a {@link java.net.SocketTimeoutException SocketTimeoutException} occurs.
     * <br><b>Default</b>: {@code true}
     *
     * <p>This value can be changed at any time with {@link JDA#setRequestTimeoutRetry(boolean) JDA.setRequestTimeoutRetry(boolean)}!
     *
     * @param  retryOnTimeout
     *         True, if the Request should retry once on a socket timeout
     *
     * @return The JDABuilder instance. Useful for chaining.
     */
    public JDABuilder setRequestTimeoutRetry(boolean retryOnTimeout)
    {
        return setFlag(ConfigFlag.RETRY_TIMEOUT, retryOnTimeout);
    }

    /**
     * Sets the token that will be used by the {@link JDA} instance to log in when
     * {@link JDABuilder#build() build()} is called.
     *
     * <h2>For {@link AccountType#BOT}</h2>
     * <ol>
     *     <li>Go to your <a href="https://discordapp.com/developers/applications/me">Discord Applications</a></li>
     *     <li>Create or select an already existing application</li>
     *     <li>Verify that it has already been turned into a Bot. If you see the "Create a Bot User" button, click it.</li>
     *     <li>Click the <i>click to reveal</i> link beside the <b>Token</b> label to show your Bot's {@code token}</li>
     * </ol>
     *
     * @param  token
     *         The token of the account that you would like to login with.
     *
     * @return The JDABuilder instance. Useful for chaining.
     */
    public JDABuilder setToken(String token)
    {
        this.token = token;
        return this;
    }

    /**
     * Sets the {@link okhttp3.OkHttpClient.Builder Builder} that will be used by JDAs requester.
     * <br>This can be used to set things such as connection timeout and proxy.
     *
     * @param  builder
     *         The new {@link okhttp3.OkHttpClient.Builder Builder} to use
     *
     * @return The JDABuilder instance. Useful for chaining.
     */
    public JDABuilder setHttpClientBuilder(OkHttpClient.Builder builder)
    {
        this.httpClientBuilder = builder;
        return this;
    }

    /**
     * Sets the {@link okhttp3.OkHttpClient OkHttpClient} that will be used by JDAs requester.
     * <br>This can be used to set things such as connection timeout and proxy.
     *
     * @param  client
     *         The new {@link okhttp3.OkHttpClient OkHttpClient} to use
     *
     * @return The JDABuilder instance. Useful for chaining.
     */
    public JDABuilder setHttpClient(OkHttpClient client)
    {
        this.httpClient = client;
        return this;
    }

    /**
     * Sets the {@link com.neovisionaries.ws.client.WebSocketFactory WebSocketFactory} that will be used by JDA's websocket client.
     * This can be used to set things such as connection timeout and proxy.
     *
     * @param  factory
     *         The new {@link com.neovisionaries.ws.client.WebSocketFactory WebSocketFactory} to use.
     *
     * @return The JDABuilder instance. Useful for chaining.
     */
    public JDABuilder setWebsocketFactory(WebSocketFactory factory)
    {
        this.wsFactory = factory;
        return this;
    }

    /**
     * Sets the {@link ScheduledExecutorService ScheduledExecutorService} that should be used in
     * the JDA rate-limit handler. Changing this can drastically change the JDA behavior for RestAction execution
     * and should be handled carefully. <b>Only change this pool if you know what you're doing.</b>
     * <br><b>This automatically disables the automatic shutdown of the rate-limit pool, you can enable
     * it using {@link #setRateLimitPool(ScheduledExecutorService, boolean) setRateLimitPool(executor, true)}</b>
     *
     * <p>This is used mostly by the Rate-Limiter to handle backoff delays by using scheduled executions.
     * Besides that it is also used by planned execution for {@link RestAction#queueAfter(long, TimeUnit)}
     * and similar methods.
     *
     * <p>Default: {@link ScheduledThreadPoolExecutor} with 5 threads.
     *
     * @param  pool
     *         The thread-pool to use for rate-limit handling
     *
     * @return The JDABuilder instance. Useful for chaining.
     */
    public JDABuilder setRateLimitPool(ScheduledExecutorService pool)
    {
        return setRateLimitPool(pool, pool == null);
    }

    /**
     * Sets the {@link ScheduledExecutorService ScheduledExecutorService} that should be used in
     * the JDA rate-limit handler. Changing this can drastically change the JDA behavior for RestAction execution
     * and should be handled carefully. <b>Only change this pool if you know what you're doing.</b>
     *
     * <p>This is used mostly by the Rate-Limiter to handle backoff delays by using scheduled executions.
     * Besides that it is also used by planned execution for {@link RestAction#queueAfter(long, TimeUnit)}
     * and similar methods.
     *
     * <p>Default: {@link ScheduledThreadPoolExecutor} with 5 threads.
     *
     * @param  pool
     *         The thread-pool to use for rate-limit handling
     * @param  automaticShutdown
     *         Whether {@link JDA#shutdown()} should shutdown this pool
     *
     * @return The JDABuilder instance. Useful for chaining.
     */
    public JDABuilder setRateLimitPool(ScheduledExecutorService pool, boolean automaticShutdown)
    {
        this.rateLimitPool = pool;
        this.shutdownRateLimitPool = automaticShutdown;
        return this;
    }

    /**
     * Sets the {@link ScheduledExecutorService ScheduledExecutorService} used by
     * the main WebSocket connection for workers. These workers spend most of their lifetime
     * sleeping because they only activate for sending messages over the gateway.
     * <br><b>Only change this pool if you know what you're doing.
     * <br>This automatically disables the automatic shutdown of the main-ws pool, you can enable
     * it using {@link #setGatewayPool(ScheduledExecutorService, boolean) setGatewayPool(pool, true)}</b>
     *
     * <p>This is used to send various forms of session updates such as:
     * <ul>
     *     <li>Voice States - (Dis-)Connecting from channels</li>
     *     <li>Presence - Changing current activity or online status</li>
     *     <li>Guild Setup - Requesting Members of newly joined guilds</li>
     *     <li>Heartbeats - Regular updates to keep the connection alive (usually once a minute)</li>
     * </ul>
     * When nothing has to be sent the pool will only be used every 500 milliseconds to check the queue for new payloads.
     * Once a new payload is sent we switch to "rapid mode" which means more tasks will be submitted until no more payloads
     * have to be sent.
     *
     * <p>Default: {@link ScheduledThreadPoolExecutor} with 1 thread
     *
     * @param  pool
     *         The thread-pool to use for WebSocket workers
     *
     * @return The JDABuilder instance. Useful for chaining.
     */
    
    public JDABuilder setGatewayPool( ScheduledExecutorService pool)
    {
        return setGatewayPool(pool, pool == null);
    }

    /**
     * Sets the {@link ScheduledExecutorService ScheduledExecutorService} used by
     * the main WebSocket connection for workers. These workers spend most of their lifetime
     * sleeping because they only activate for sending messages over the gateway.
     * <br><b>Only change this pool if you know what you're doing.</b>
     *
     * <p>This is used to send various forms of session updates such as:
     * <ul>
     *     <li>Voice States - (Dis-)Connecting from channels</li>
     *     <li>Presence - Changing current activity or online status</li>
     *     <li>Guild Setup - Requesting Members of newly joined guilds</li>
     *     <li>Heartbeats - Regular updates to keep the connection alive (usually once a minute)</li>
     * </ul>
     * When nothing has to be sent the pool will only be used every 500 milliseconds to check the queue for new payloads.
     * Once a new payload is sent we switch to "rapid mode" which means more tasks will be submitted until no more payloads
     * have to be sent.
     *
     * <p>Default: {@link ScheduledThreadPoolExecutor} with 1 thread
     *
     * @param  pool
     *         The thread-pool to use for WebSocket workers
     * @param  automaticShutdown
     *         Whether {@link JDA#shutdown()} should shutdown this pool
     *
     * @return The JDABuilder instance. Useful for chaining.
     */
    
    public JDABuilder setGatewayPool( ScheduledExecutorService pool, boolean automaticShutdown)
    {
        this.mainWsPool = pool;
        this.shutdownMainWsPool = automaticShutdown;
        return this;
    }

    public JDABuilder setCallbackPool( ExecutorService executor)
    {
        return setCallbackPool(executor, executor == null);
    }

    
    public JDABuilder setCallbackPool( ExecutorService executor, boolean automaticShutdown)
    {
        this.callbackPool = executor;
        this.shutdownCallbackPool = automaticShutdown;
        return this;
    }

    /**
     * If enabled, JDA will separate the bulk delete event into individual delete events, but this isn't as efficient as
     * handling a single event would be. It is recommended that BulkDelete Splitting be disabled and that the developer
     * should instead handle the {@link MessageBulkDeleteEvent MessageBulkDeleteEvent}
     *
     * <p>Default: <b>true (enabled)</b>
     *
     * @param  enabled
     *         True - The MESSAGE_DELETE_BULK will be split into multiple individual MessageDeleteEvents.
     *
     * @return The JDABuilder instance. Useful for chaining.
     */
    
    public JDABuilder setBulkDeleteSplittingEnabled(boolean enabled)
    {
        return setFlag(ConfigFlag.BULK_DELETE_SPLIT, enabled);
    }

    /**
     * Enables/Disables the use of a Shutdown hook to clean up JDA.
     * <br>When the Java program closes shutdown hooks are run. This is used as a last-second cleanup
     * attempt by JDA to properly close connections.
     *
     * <p>Default: <b>true (enabled)</b>
     *
     * @param  enable
     *         True (default) - use shutdown hook to clean up JDA if the Java program is closed.
     *
     * @return Return the {@link JDABuilder JDABuilder } instance. Useful for chaining.
     */
    
    public JDABuilder setEnableShutdownHook(boolean enable)
    {
        return setFlag(ConfigFlag.SHUTDOWN_HOOK, enable);
    }

    /**
     * Sets whether or not JDA should try to reconnect if a connection-error is encountered.
     * <br>This will use an incremental reconnect (timeouts are increased each time an attempt fails).
     *
     * Default: <b>true (enabled)</b>
     *
     * @param  autoReconnect
     *         If true - enables autoReconnect
     *
     * @return The JDABuilder instance. Useful for chaining.
     */
    
    public JDABuilder setAutoReconnect(boolean autoReconnect)
    {
        return setFlag(ConfigFlag.AUTO_RECONNECT, autoReconnect);
    }

    /**
     * Changes the internally used EventManager.
     * <br>There are 2 provided Implementations:
     * <ul>
     *     <li>{@link InterfacedEventManager InterfacedEventManager} which uses the Interface
     *     {@link EventListener EventListener} (tip: use the {@link ListenerAdapter ListenerAdapter}).
     *     <br>This is the default EventManager.</li>
     *
     *     <li>{@link AnnotatedEventManager AnnotatedEventManager} which uses the Annotation
     *         {@link SubscribeEvent @SubscribeEvent} to mark the methods that listen for events.</li>
     * </ul>
     * <br>You can also create your own EventManager (See {@link IEventManager}).
     *
     * @param  manager
     *         The new {@link IEventManager} to use.
     *
     * @return The JDABuilder instance. Useful for chaining.
     */
    
    public JDABuilder setEventManager( IEventManager manager)
    {
        this.eventManager = manager;
        return this;
    }

    /**
     * Changes the factory used to create {@link IAudioSendSystem IAudioSendSystem}
     * objects which handle the sending loop for audio packets.
     * <br>By default, JDA uses {@link DefaultSendFactory DefaultSendFactory}.
     *
     * @param  factory
     *         The new {@link IAudioSendFactory IAudioSendFactory} to be used
     *         when creating new {@link IAudioSendSystem} objects.
     *
     * @return The JDABuilder instance. Useful for chaining.
     */
    
    public JDABuilder setAudioSendFactory( IAudioSendFactory factory)
    {
        this.audioSendFactory = factory;
        return this;
    }

    /**
     * Sets whether or not we should mark our session as afk
     * <br>This value can be changed at any time in the {@link Presence Presence} from a JDA instance.
     *
     * @param  idle
     *         boolean value that will be provided with our IDENTIFY package to mark our session as afk or not. <b>(default false)</b>
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @see    Presence#setIdle(boolean) Presence.setIdle(boolean)
     */
    
    public JDABuilder setIdle(boolean idle)
    {
        this.idle = idle;
        return this;
    }

    /**
     * Sets the {@link Activity Activity} for our session.
     * <br>This value can be changed at any time in the {@link Presence Presence} from a JDA instance.
     *
     * <p><b>Hint:</b> You can create a {@link Activity Activity} object using
     * {@link Activity#playing(String)} or {@link Activity#streaming(String, String)}.
     *
     * @param  activity
     *         An instance of {@link Activity Activity} (null allowed)
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @see    Presence#setActivity(Activity)  Presence.setActivity(Activity)
     */
    
    public JDABuilder setActivity( Activity activity)
    {
        this.activity = activity;
        return this;
    }

    /**
     * Sets the {@link OnlineStatus OnlineStatus} our connection will display.
     * <br>This value can be changed at any time in the {@link Presence Presence} from a JDA instance.
     *
     * <p><b>Note:</b>This will not take affect for {@link AccountType#CLIENT AccountType.CLIENT}
     * if the status specified in the user_settings is not "online" as it is overriding our identify status.
     *
     * @param  status
     *         Not-null OnlineStatus (default online)
     *
     * @throws IllegalArgumentException
     *         if the provided OnlineStatus is null or {@link OnlineStatus#UNKNOWN UNKNOWN}
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @see    Presence#setStatus(OnlineStatus) Presence.setStatus(OnlineStatus)
     */
    
    @SuppressWarnings("ConstantConditions") // we have to enforce the nonnull at runtime
    public JDABuilder setStatus( OnlineStatus status)
    {
        if (status == null || status == OnlineStatus.UNKNOWN)
            throw new IllegalArgumentException("OnlineStatus cannot be null or unknown!");
        this.status = status;
        return this;
    }

    /**
     * Adds all provided listeners to the list of listeners that will be used to populate the {@link JDA JDA} object.
     * <br>This uses the {@link InterfacedEventManager InterfacedEventListener} by default.
     * <br>To switch to the {@link AnnotatedEventManager AnnotatedEventManager},
     * use {@link #setEventManager(IEventManager) setEventManager(new AnnotatedEventManager())}.
     *
     * <p><b>Note:</b> When using the {@link InterfacedEventManager InterfacedEventListener} (default),
     * given listener(s) <b>must</b> be instance of {@link EventListener EventListener}!
     *
     * @param   listeners
     *          The listener(s) to add to the list.
     *
     * @throws java.lang.IllegalArgumentException
     *         If either listeners or one of it's objects is {@code null}.
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @see    JDA#addEventListener(Object...) JDA.addEventListener(Object...)
     */
    
    public JDABuilder addEventListeners( Object... listeners)
    {
        Checks.noneNull(listeners, "listeners");

        Collections.addAll(this.listeners, listeners);
        return this;
    }

    /**
     * Removes all provided listeners from the list of listeners.
     *
     * @param  listeners
     *         The listener(s) to remove from the list.
     *
     * @throws java.lang.IllegalArgumentException
     *         If either listeners or one of it's objects is {@code null}.
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @see    JDA#removeEventListener(Object...) JDA.removeEventListener(Object...)
     */
    
    public JDABuilder removeEventListeners( Object... listeners)
    {
        Checks.noneNull(listeners, "listeners");

        this.listeners.removeAll(Arrays.asList(listeners));
        return this;
    }

    /**
     * Sets the maximum amount of time that JDA will back off to wait when attempting to reconnect the MainWebsocket.
     * <br>Provided value must be 32 or greater.
     *
     * @param  maxReconnectDelay
     *         The maximum amount of time that JDA will wait between reconnect attempts in seconds.
     *
     * @throws java.lang.IllegalArgumentException
     *         Thrown if the provided {@code maxReconnectDelay} is less than 32.
     *
     * @return The JDABuilder instance. Useful for chaining.
     */
    
    public JDABuilder setMaxReconnectDelay(int maxReconnectDelay)
    {
        Checks.check(maxReconnectDelay >= 32, "Max reconnect delay must be 32 seconds or greater. You provided %d.", maxReconnectDelay);

        this.maxReconnectDelay = maxReconnectDelay;
        return this;
    }

    /**
     * This will enable sharding mode for JDA.
     * <br>In sharding mode, guilds are split up and assigned one of multiple shards (clients).
     * <br>The shardId that receives all stuff related to given bot is calculated as follows: shardId == (guildId {@literal >>} 22) % shardTotal;
     * <br><b>PMs are only sent to shard 0.</b>
     *
     * <p>Please note, that a shard will not know about guilds which are not assigned to it.
     *
     * <p><b>It is not possible to use sharding with an account for {@link AccountType#CLIENT AccountType.CLIENT}!</b>
     *
     * @param  shardId
     *         The id of this shard (starting at 0).
     * @param  shardTotal
     *         The number of overall shards.
     *
     * @throws AccountTypeException
     *         If this is used on a JDABuilder for {@link AccountType#CLIENT AccountType.CLIENT}
     * @throws java.lang.IllegalArgumentException
     *         If the provided shard configuration is invalid
     *         ({@code 0 <= shardId < shardTotal} with {@code shardTotal > 0})
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @see    JDA#getShardInfo() JDA.getShardInfo()
     * @see    ShardManager ShardManager
     */
    
    public JDABuilder useSharding(int shardId, int shardTotal)
    {
        AccountTypeException.check(accountType, AccountType.BOT);
        Checks.notNegative(shardId, "Shard ID");
        Checks.positive(shardTotal, "Shard Total");
        Checks.check(shardId < shardTotal,
                "The shard ID must be lower than the shardTotal! Shard IDs are 0-based.");
        shardInfo = new JDA.ShardInfo(shardId, shardTotal);
        return this;
    }

    /**
     * Sets the {@link SessionController SessionController}
     * for this JDABuilder instance. This can be used to sync behaviour and state between shards
     * of a bot and should be one and the same instance on all builders for the shards.
     * <br>When {@link #useSharding(int, int)} is enabled, this is set by default.
     *
     * <p>When set, this allows the builder to build shards with respect to the login ratelimit automatically.
     *
     * @param  controller
     *         The {@link SessionController SessionController} to use
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @see    SessionControllerAdapter SessionControllerAdapter
     */
    
    public JDABuilder setSessionController( SessionController controller)
    {
        this.controller = controller;
        return this;
    }

    /**
     * Configures a custom voice dispatch handler which handles audio connections.
     *
     * @param  interceptor
     *         The new voice dispatch handler, or null to use the default
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @since 4.0.0
     *
     * @see    VoiceDispatchInterceptor
     */
    
    public JDABuilder setVoiceDispatchInterceptor( VoiceDispatchInterceptor interceptor)
    {
        this.voiceDispatchInterceptor = interceptor;
        return this;
    }

    /**
     * The {@link ChunkingFilter} to filter which guilds should use member chunking.
     * <br>By default this uses {@link ChunkingFilter#ALL}.
     *
     * <p>This filter is useless when {@link #setGuildSubscriptionsEnabled(boolean)} is false.
     *
     * @param  filter
     *         The filter to apply
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @since  4.1.0
     *
     * @see    ChunkingFilter#NONE
     * @see    ChunkingFilter#include(long...)
     * @see    ChunkingFilter#exclude(long...)
     */
    
    public JDABuilder setChunkingFilter( ChunkingFilter filter)
    {
        this.chunkingFilter = filter == null ? ChunkingFilter.ALL : filter;
        return this;
    }

    /**
     * Enable typing and presence update events.
     * <br>These events cover the majority of traffic happening on the gateway and thus cause a lot
     * of bandwidth usage. Disabling these events means the cache for users might become outdated since
     * user properties are only updated by presence updates.
     * <br>Default: true
     *
     * <h2>Notice</h2>
     * This disables the majority of member cache and related events. If anything in your project
     * relies on member state you should keep this enabled.
     *
     * @param  enabled
     *         True, if guild subscriptions should be enabled
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @since  4.1.0
     */
    
    public JDABuilder setGuildSubscriptionsEnabled(boolean enabled)
    {
        return setFlag(ConfigFlag.GUILD_SUBSCRIPTIONS, enabled);
    }

    /**
     * Decides the total number of members at which a guild should start to use lazy loading.
     * <br>This is limited to a number between 50 and 250 (inclusive).
     * If the {@link #setChunkingFilter(ChunkingFilter) chunking filter} is set to {@link ChunkingFilter#ALL}
     * this should be set to {@code 250} (default) to minimize the amount of guilds that need to request members.
     *
     * @param  threshold
     *         The threshold in {@code [50, 250]}
     *
     * @return The JDABuilder instance. Useful for chaining.
     *
     * @since  4.1.0
     */
    
    public JDABuilder setLargeThreshold(int threshold)
    {
        this.largeThreshold = Math.max(50, Math.min(250, threshold)); // enforce 50 <= t <= 250
        return this;
    }

    /**
     * The maximum size, in bytes, of the buffer used for decompressing discord payloads.
     * <br>If the maximum buffer size is exceeded a new buffer will be allocated instead.
     * <br>Setting this to {@link Integer#MAX_VALUE} would imply the buffer will never be resized unless memory starvation is imminent.
     * <br>Setting this to {@code 0} would imply the buffer would need to be allocated again for every payload (not recommended).
     *
     * <p>Default: {@code 2048}
     *
     * @param  bufferSize
     *         The maximum size the buffer should allow to retain
     *
     * @throws IllegalArgumentException
     *         If the provided buffer size is negative
     *
     * @return The JDABuilder instance. Useful for chaining.
     */
    
    public JDABuilder setMaxBufferSize(int bufferSize)
    {
        Checks.notNegative(bufferSize, "The buffer size");
        this.maxBufferSize = bufferSize;
        return this;
    }

    /**
     * Builds a new {@link JDA} instance and uses the provided token to start the login process.
     * <br>The login process runs in a different thread, so while this will return immediately, {@link JDA} has not
     * finished loading, thus many {@link JDA} methods have the chance to return incorrect information.
     * For example {@link JDA#getGuilds()} might return an empty list or {@link JDA#getUserById(long)} might return null
     * for arbitrary user IDs.
     *
     * <p>If you wish to be sure that the {@link JDA} information is correct, please use
     * {@link JDA#awaitReady() JDA.awaitReady()} or register an
     * {@link EventListener EventListener} to listen for the
     * {@link ReadyEvent ReadyEvent}.
     *
     * @throws LoginException
     *         If the provided token is invalid.
     * @throws IllegalArgumentException
     *         If the provided token is empty or null.
     *
     * @return A {@link JDA} instance that has started the login process. It is unknown as
     *         to whether or not loading has finished when this returns.
     *
     * @see    JDA#awaitReady()
     */
    
    public JDA build() throws LoginException
    {
        OkHttpClient httpClient = this.httpClient;
        if (httpClient == null)
        {
            if (this.httpClientBuilder == null)
                this.httpClientBuilder = new OkHttpClient.Builder();
            httpClient = this.httpClientBuilder.build();
        }

        WebSocketFactory wsFactory = this.wsFactory == null ? new WebSocketFactory() : this.wsFactory;

        if (controller == null && shardInfo != null)
            controller = new SessionControllerAdapter();

        AuthorizationConfig authConfig = new AuthorizationConfig(accountType, token);
        ThreadingConfig threadingConfig = new ThreadingConfig();
        threadingConfig.setCallbackPool(callbackPool, shutdownCallbackPool);
        threadingConfig.setGatewayPool(mainWsPool, shutdownMainWsPool);
        threadingConfig.setRateLimitPool(rateLimitPool, shutdownRateLimitPool);
        SessionConfig sessionConfig = new SessionConfig(controller, httpClient, wsFactory, voiceDispatchInterceptor, flags, maxReconnectDelay, largeThreshold);
        MetaConfig metaConfig = new MetaConfig(maxBufferSize, contextMap, cacheFlags, flags);

        JDAImpl jda = new JDAImpl(authConfig, sessionConfig, threadingConfig, metaConfig);
        jda.setChunkingFilter(chunkingFilter);

        if (eventManager != null)
            jda.setEventManager(eventManager);

        if (audioSendFactory != null)
            jda.setAudioSendFactory(audioSendFactory);

        listeners.forEach(jda::addEventListener);
        jda.setStatus(JDA.Status.INITIALIZED);  //This is already set by JDA internally, but this is to make sure the listeners catch it.

        // Set the presence information before connecting to have the correct information ready when sending IDENTIFY
        ((PresenceImpl) jda.getPresence())
                .setCacheActivity(activity)
                .setCacheIdle(idle)
                .setCacheStatus(status);
        jda.login(shardInfo, compression, true);
        return jda;
    }

    private JDABuilder setFlag(ConfigFlag flag, boolean enable)
    {
        if (enable)
            this.flags.add(flag);
        else
            this.flags.remove(flag);
        return this;
    }
}
