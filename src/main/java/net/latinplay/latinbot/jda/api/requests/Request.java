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

package net.latinplay.latinbot.jda.api.requests;

import net.latinplay.latinbot.jda.api.audit.ThreadLocalReason;
import net.latinplay.latinbot.jda.api.events.ExceptionEvent;
import net.latinplay.latinbot.jda.api.events.http.HttpRequestEvent;
import net.latinplay.latinbot.jda.api.exceptions.ContextException;
import net.latinplay.latinbot.jda.api.exceptions.ErrorResponseException;
import net.latinplay.latinbot.jda.api.exceptions.RateLimitedException;
import net.latinplay.latinbot.jda.internal.JDAImpl;
import net.latinplay.latinbot.jda.internal.requests.CallbackContext;
import net.latinplay.latinbot.jda.internal.requests.RestActionImpl;
import net.latinplay.latinbot.jda.internal.requests.Route;
import okhttp3.RequestBody;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class Request<T>
{
    private final JDAImpl api;
    private final RestActionImpl<T> restAction;
    private final Consumer<? super T> onSuccess;
    private final Consumer<? super Throwable> onFailure;
    private final BooleanSupplier checks;
    private final boolean shouldQueue;
    private final Route.CompiledRoute route;
    private final RequestBody body;
    private final Object rawBody;
    private final CaseInsensitiveMap<String, String> headers;

    private final String localReason;

    private boolean isCanceled = false;

    public Request(
            RestActionImpl<T> restAction, Consumer<? super T> onSuccess, Consumer<? super Throwable> onFailure,
            BooleanSupplier checks, boolean shouldQueue, RequestBody body, Object rawBody,
            Route.CompiledRoute route, CaseInsensitiveMap<String, String> headers)
    {
        this.restAction = restAction;
        this.onSuccess = onSuccess;
        if (onFailure instanceof ContextException.ContextConsumer)
            this.onFailure = onFailure;
        else if (RestActionImpl.isPassContext())
            this.onFailure = ContextException.here(onFailure);
        else
            this.onFailure = onFailure;
        this.checks = checks;
        this.shouldQueue = shouldQueue;
        this.body = body;
        this.rawBody = rawBody;
        this.route = route;
        this.headers = headers;

        this.api = (JDAImpl) restAction.getJDA();
        this.localReason = ThreadLocalReason.getCurrent();
    }

    public void onSuccess(T successObj)
    {
        api.getCallbackPool().execute(() ->
        {
            try (ThreadLocalReason.Closable __ = ThreadLocalReason.closable(localReason);
                 CallbackContext ___ = CallbackContext.getInstance())
            {
                onSuccess.accept(successObj);
            }
            catch (Throwable t)
            {
                RestActionImpl.LOG.error("Encountered error while processing success consumer", t);
                if (t instanceof Error)
                    api.handleEvent(new ExceptionEvent(api, t, true));
            }
        });
    }

    public void onFailure(Response response)
    {
        if (response.code == 429)
        {
            onFailure(new RateLimitedException(route, response.retryAfter));
        }
        else
        {
            onFailure(ErrorResponseException.create(
                    ErrorResponse.fromJSON(response.optObject().orElse(null)), response));
        }
    }

    public void onFailure(Throwable failException)
    {
        api.getCallbackPool().execute(() ->
        {
            try (ThreadLocalReason.Closable __ = ThreadLocalReason.closable(localReason);
                 CallbackContext ___ = CallbackContext.getInstance())
            {
                onFailure.accept(failException);
                if (failException instanceof Error)
                    api.handleEvent(new ExceptionEvent(api, failException, false));
            }
            catch (Throwable t)
            {
                RestActionImpl.LOG.error("Encountered error while processing failure consumer", t);
                if (t instanceof Error)
                    api.handleEvent(new ExceptionEvent(api, t, true));
            }
        });
    }

    
    public JDAImpl getJDA()
    {
        return api;
    }

    
    public RestAction<T> getRestAction()
    {
        return restAction;
    }

    
    public Consumer<? super T> getOnSuccess()
    {
        return onSuccess;
    }

    
    public Consumer<? super Throwable> getOnFailure()
    {
        return onFailure;
    }

    public boolean runChecks()
    {
        return checks == null || checks.getAsBoolean();
    }

    
    public CaseInsensitiveMap<String, String> getHeaders()
    {
        return headers;
    }

    
    public Route.CompiledRoute getRoute()
    {
        return route;
    }

    
    public RequestBody getBody()
    {
        return body;
    }

    
    public Object getRawBody()
    {
        return rawBody;
    }

    public boolean shouldQueue()
    {
        return shouldQueue;
    }

    public void cancel()
    {
        this.isCanceled = true;
    }

    public boolean isCanceled()
    {
        return isCanceled;
    }

    public void handleResponse( Response response)
    {
        restAction.handleResponse(response, this);
        api.handleEvent(new HttpRequestEvent(this, response));
    }
}
