/*
 * Copyright 2015-2019 Austin Keener, Michael Ritter, Florian Spieß, and the JDA contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.latinplay.latinbot.jda.api.utils;

import net.latinplay.latinbot.jda.annotations.DeprecatedSince;
import net.latinplay.latinbot.jda.annotations.ForRemoval;
import net.latinplay.latinbot.jda.annotations.ReplaceWith;
import net.latinplay.latinbot.jda.api.JDA;
import net.latinplay.latinbot.jda.api.JDABuilder;
import net.latinplay.latinbot.jda.api.sharding.DefaultShardManager;
import net.latinplay.latinbot.jda.api.sharding.ShardManager;
import net.latinplay.latinbot.jda.internal.utils.tuple.Pair;

/**
 * Controls states and behaviour of one or multiple {@link JDA JDA} instances.
 * <br>One instance of this should be used when sharding a bot account in order to keep track of session information
 * between shards.
 *
 * <p>The {@link SessionControllerAdapter SessionControllerAdapter} provides
 * a default implementation that can be extended and overridden.
 *
 * <h2>States {@literal &} Behaviour</h2>
 * <b>Identify Ratelimit Handling</b>
 * <br>This will enable handling of (re-)connecting gateway sessions.
 *
 * <p><b>Global REST Ratelimit</b>
 * <br>The global REST ratelimit is not bound to a single session and should be
 * handled on all JDA instances. This controller will receive updates of this ratelimit through {@link #setGlobalRatelimit(long)}
 * and should report the last ratelimit information it received through {@link #getGlobalRatelimit()}.
 *
 * <p><b>Gateway Provider</b>
 * <br>This provider can be used to change the gateway retrieval (using cache, http, or static) and
 * allows to set a custom gateway endpoint. <b>Use carefully.</b>
 *
 * <h2>Examples</h2>
 *
 * <b>Using {@link JDABuilder JDABuilder}</b>
 * <br>
 * <pre><code>
 * JDABuilder builder = new JDABuilder(AccountType.BOT).setToken(BOT_TOKEN);
 * builder.setSessionController(new SessionControllerAdapter() {
 *     {@literal @Override}
 *     public void appendSession(SessionConnectNode node) {
 *         System.out.println("[SessionController] Adding SessionConnectNode to Queue!");
 *         super.appendSession(node);
 *     }
 * });
 * builder.addEventListeners(myListener);
 * for (int i = 0; i {@literal <} 10; i++) {
 *     builder.useSharding(i, 10).buildAsync();
 * }
 * </code></pre>
 *
 * <p><b>Using {@link ShardManager ShardManager}</b>
 * <br>
 * <pre><code>
 * DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder();
 * builder.setToken(BOT_TOKEN);
 * builder.setSessionController(new SessionControllerAdapter() {
 *     {@literal @Override}
 *     public {@literal Pair<String, Integer>} getGatewayBot(JDA api) {
 *         return Pair.of(getGateway(), 10);
 *     }
 * });
 * builder.addEventListeners(myListener);
 * builder.build();
 * </code></pre>
 */
public interface SessionController
{
    /**
     * The default delay (in seconds) to wait between running {@link SessionController.SessionConnectNode SessionConnectNodes}
     */
    int IDENTIFY_DELAY = 5;

    /**
     * Called by a JDA session when a WebSocket should be started. (Connecting and Reconnecting)
     * <br>This should only add the node to a queue and execute the queue with respect to the {@link #IDENTIFY_DELAY}.
     *
     * @param  node
     *         The {@link SessionController.SessionConnectNode SessionConnectNode}
     */
    void appendSession( SessionConnectNode node);

    /**
     * Called by a JDA session when a shutdown has been requested.
     * <br>When this happened the {@link SessionController.SessionConnectNode#run(boolean) SessionConnectNode.run(boolean)}
     * will be a no-op and does not contribute to the {@link #IDENTIFY_DELAY}.
     *
     * @param node
     *        The {@link SessionController.SessionConnectNode SessionConnectNode} to remove from the queue.
     */
    void removeSession( SessionConnectNode node);

    /**
     * Provides the cross-session global REST ratelimit it received through {@link #setGlobalRatelimit(long)}.
     *
     * @return The current global REST ratelimit or -1 if unset
     */
    long getGlobalRatelimit();

    /**
     * Called by the RateLimiter if the global rest ratelimit has changed.
     *
     * @param ratelimit
     *        The new global ratelimit
     */
    void setGlobalRatelimit(long ratelimit);

    /**
     * Called by a JDA session when a new gateway session starts (Connecting, Reconnecting).
     * <br>Should provide the gateway endpoint (wss) to connect to.
     *
     * @param  api
     *         The current JDA instance (used for RestActions and ShardInfo)
     *
     * @return The gateway endpoint
     */
    
    String getGateway( JDA api);

    /**
     * Called by {@link DefaultShardManager DefaultShardManager}
     * when a new shards is starting.
     * <br>Should provide a {@link Pair Pair} with {@code (gateway, shardTotal)}.
     *
     * @param  api
     *         The current JDA instance (used for RestActions and ShardInfo)
     *
     * @return The Pair consisting of the gateway endpoint to connect to and the shardTotal
     *
     * @see    #getGateway(JDA)
     *
     * @deprecated
     *         Use {@link #getShardedGateway(JDA)} instead, an implementation for this is ignored
     *         if {@link #getShardedGateway(JDA)} is implemented instead.
     */
    
    @Deprecated
    @ForRemoval
    @DeprecatedSince("4.0.0")
    @ReplaceWith("getShardedGateway(api)")
    @SuppressWarnings("DeprecatedIsStillUsed")
    Pair<String, Integer> getGatewayBot( JDA api);

    /**
     * Called by {@link DefaultShardManager DefaultShardManager}
     * when a new shards is starting.
     * <br>Should provide a {@link ShardedGateway} with {@code (gateway, shardTotal)}.
     *
     * @param  api
     *         The current JDA instance (used for RestActions and ShardInfo)
     *
     * @return The ShardedGateway instance consisting of the gateway endpoint to connect to and the shardTotal
     *
     * @see    #getGateway(JDA)
     */
    
    @SuppressWarnings({"deprecation", "RedundantSuppression"})
    default ShardedGateway getShardedGateway( JDA api)
    {
        Pair<String, Integer> tuple = getGatewayBot(api);
        return new ShardedGateway(tuple.getLeft(), tuple.getRight());
    }

    /**
     * POJO containing the gateway endpoint and recommended shard total for a shard manager.
     */
    class ShardedGateway
    {
        private final String url;
        private final int shardTotal;

        /**
         * Creates a new GatewayBot instance with the provided properties
         *
         * @param url
         *        The gateway endpoint (wss)
         * @param shardTotal
         *        The recommended shard total
         */
        public ShardedGateway(String url, int shardTotal)
        {
            this.url = url;
            this.shardTotal = shardTotal;
        }

        /**
         * The gateway endpoint
         *
         * @return The endpoint
         */
        public String getUrl()
        {
            return url;
        }

        /**
         * The recommended shard total
         *
         * @return The shard total
         */
        public int getShardTotal()
        {
            return shardTotal;
        }
    }

    /**
     * Represents a WebSocketClient request to start a session.
     * <br>Not implemented by library user.
     *
     * <p><b>Note: None of the provided session nodes can be resumed, the resume timeframe has already passed</b>
     */
    interface SessionConnectNode
    {
        /**
         * Whether this node is reconnecting. Can be used to setup a priority based system.
         *
         * @return True, if this session is reconnecting
         */
        boolean isReconnect();

        /**
         * The {@link JDA JDA} instance for this request
         *
         * @return The JDA instance
         */
        
        JDA getJDA();

        /**
         * The {@link JDA.ShardInfo ShardInfo} for this request.
         * <br>Can be used for a priority system.
         *
         * @return The ShardInfo
         */
        
        JDA.ShardInfo getShardInfo();

        /**
         * When called, establishes the session.
         * <br>This will return once the required payload to start the session has been delivered.
         *
         * @param  isLast
         *         True, if this is the last node in a queue worker.
         *         When true this will not wait for the payload to be delivered.
         *
         * @throws InterruptedException
         *         If the calling thread is interrupted
         */
        void run(boolean isLast) throws InterruptedException;
    }
}
