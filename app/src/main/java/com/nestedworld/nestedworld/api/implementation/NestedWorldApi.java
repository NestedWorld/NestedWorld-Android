package com.nestedworld.nestedworld.api.implementation;

import android.content.Context;

import com.nestedworld.nestedworld.api.models.User;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public class NestedWorldApi {
    private static com.nestedworld.nestedworld.api.implementation.NestedWorldApi mSingleton;
    private final String TAG = getClass().getSimpleName();
    private Context mContext;
    private NestedWorldApiInterface mClient;

    /*
    ** Constructor
     */
    public NestedWorldApi(final Context context) {
        if (mSingleton != null) {
            return;
        }

        //init API
        mContext = context;
    }

    /*
    ** Singleton
     */
    public static com.nestedworld.nestedworld.api.implementation.NestedWorldApi getInstance(final Context context) {
        if (mSingleton == null) {
            mSingleton = new com.nestedworld.nestedworld.api.implementation.NestedWorldApi(context);
        }
        return mSingleton;
    }

    private void init() {
        // Define the interceptor, add authentication headers
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                //TODO completer les header
                request.addHeader("X-User-Id", "userId");
                request.addHeader("X-User-Email", "userEmail");
                request.addHeader("X-User-Token", "authenticationToken");
                request.addHeader("X-User-Push-Token", "pushToken");
            }
        };

        // Add interceptor when building adapter
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constant.BASE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(requestInterceptor)
                .build();
        mClient = restAdapter.create(NestedWorldApiInterface.class);
    }

    /*
    ** Interface implementation
     */
    public void signUp(final User user, final Callback<User> callback) {
        mClient.signUp(user.getEmail(), user.getPassword(), callback);
    }

    /*
    ** Interface
     */

    public interface NestedWorldApiInterface {
        @POST(Constant.USER_SIGN_UP)
        @FormUrlEncoded
        void signUp(
                @Field("user[email]") String email,
                @Field("user[password]") String password,
                Callback<User> callback);
    }
}
