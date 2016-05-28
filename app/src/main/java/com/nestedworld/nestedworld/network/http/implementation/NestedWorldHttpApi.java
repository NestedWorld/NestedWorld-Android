package com.nestedworld.nestedworld.network.http.implementation;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.GsonBuilder;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.helpers.session.SessionManager;
import com.nestedworld.nestedworld.models.Region;
import com.nestedworld.nestedworld.models.Session;
import com.nestedworld.nestedworld.network.http.callback.Callback;
import com.nestedworld.nestedworld.network.http.models.request.users.auth.ForgotPasswordRequest;
import com.nestedworld.nestedworld.network.http.models.request.users.auth.RegisterRequest;
import com.nestedworld.nestedworld.network.http.models.request.users.auth.SignInRequest;
import com.nestedworld.nestedworld.network.http.models.response.monsters.MonstersResponse;
import com.nestedworld.nestedworld.network.http.models.response.places.PlacesResponse;
import com.nestedworld.nestedworld.network.http.models.response.places.regions.RegionResponse;
import com.nestedworld.nestedworld.network.http.models.response.places.regions.RegionsResponse;
import com.nestedworld.nestedworld.network.http.models.response.users.UserResponse;
import com.nestedworld.nestedworld.network.http.models.response.users.auth.ForgotPasswordResponse;
import com.nestedworld.nestedworld.network.http.models.response.users.auth.LogoutResponse;
import com.nestedworld.nestedworld.network.http.models.response.users.auth.RegisterResponse;
import com.nestedworld.nestedworld.network.http.models.response.users.auth.SignInResponse;
import com.nestedworld.nestedworld.network.http.models.response.users.friend.FriendsResponse;
import com.nestedworld.nestedworld.network.http.models.response.users.monster.UserMonsterResponse;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Simple Api containing the retrofit interface and his implementation
 */
public final class NestedWorldHttpApi {
    private static NestedWorldHttpApi mSingleton;
    private final String TAG = getClass().getSimpleName();
    private ApiInterface mClient;
    private Context mContext;

    /*
    ** Constructor
     */
    private NestedWorldHttpApi(@NonNull final Context context) {
        if (mSingleton != null) {
            return;
        }

        //init API
        init(context);
    }

    /*
    ** Singleton
     */
    public static NestedWorldHttpApi getInstance(@NonNull final Context context) {
        if (mSingleton == null) {
            mSingleton = new NestedWorldHttpApi(context);
        }
        return mSingleton;
    }

    /*
    ** Avoid leek when log-out
     */
    public static void reset() {
        mSingleton = null;
    }

    /*
    ** Private method
     */
    private void init(@NonNull final Context context) {
        LogHelper.d(TAG, "Init API(http) (end_point = " + HttpEndPoint.BASE_END_POINT + ")");

        //set the context to null for avoiding any leaks and then set the new context
        mContext = null;
        mContext = context;

        // Define a request interceptor for displaying some log
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Define a request interception for adding some custom headers
        Interceptor httpHeaderInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request.Builder requestBuilder = chain.request().newBuilder();

                Session session = SessionManager.get().getSession();
                if (session != null) {
                    requestBuilder.addHeader("X-User-Email", session.email);
                    requestBuilder.addHeader("Authorization", session.authToken);
                }

                return chain.proceed(requestBuilder.build());
            }
        };

        // We declare the client and we add our interceptors …
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(httpHeaderInterceptor)
                .addInterceptor(httpLoggingInterceptor)// we add logging interceptor as last httpHeaderInterceptor
                .build();

        // Create the converterFactory
        GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(builder.create());

        // Init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpEndPoint.BASE_END_POINT)
                .addConverterFactory(gsonConverterFactory)
                .client(client)
                .build();

        mClient = retrofit.create(ApiInterface.class);
    }

    /**
     * Interface implementation
     */
    public Call<RegisterResponse> register(@NonNull final String email, @NonNull final String password, @NonNull final String pseudo, @NonNull final Callback<RegisterResponse> callback) {
        Call<RegisterResponse> request = mClient.register(new RegisterRequest(email, password, pseudo));
        request.enqueue(callback);
        return request;
    }

    public Call<SignInResponse> signIn(@NonNull final String email, @NonNull final String password, @NonNull final Callback<SignInResponse> callback) {
        String app_token = mContext.getString(R.string.app_token);

        Call<SignInResponse> request = mClient.signIn(new SignInRequest(email, password, app_token));
        request.enqueue(callback);
        return request;
    }

    public Call<ForgotPasswordResponse> forgotPassword(@NonNull final String email, @NonNull final Callback<ForgotPasswordResponse> callback) {
        Call<ForgotPasswordResponse> request = mClient.forgotPassword(new ForgotPasswordRequest(email));
        request.enqueue(callback);
        return request;
    }

    public Call<LogoutResponse> logout(@NonNull final Callback<LogoutResponse> callback) {
        Call<LogoutResponse> request = mClient.logout();
        request.enqueue(callback);
        return request;
    }

    public Call<MonstersResponse> getMonsters(@NonNull final Callback<MonstersResponse> callback) {
        Call<MonstersResponse> request = mClient.getMonsters();
        request.enqueue(callback);
        return request;
    }

    public Call<UserResponse> getUserInfo(@NonNull final Callback<UserResponse> callback) {
        Call<UserResponse> request = mClient.getUserInfo();
        request.enqueue(callback);
        return request;
    }

    public Call<PlacesResponse> getPlaces(@NonNull Callback<PlacesResponse> callback) {
        Call<PlacesResponse> request = mClient.getPlaces();
        request.enqueue(callback);
        return request;
    }

    public Call<RegionsResponse> getRegions(@NonNull final Callback<RegionsResponse> callback) {
        Call<RegionsResponse> request = mClient.getRegions();
        request.enqueue(callback);
        return request;
    }

    public Call<RegionResponse> getRegionDetails(@NonNull final Region region, @NonNull final Callback<RegionResponse> callback) {
        Call<RegionResponse> request = mClient.getRegionDetail(region.url);
        request.enqueue(callback);
        return request;
    }

    public Call<FriendsResponse> getFriends(@NonNull final Callback<FriendsResponse> callback) {
        Call<FriendsResponse> request = mClient.getFriends();
        request.enqueue(callback);
        return request;
    }

    public Call<UserMonsterResponse> getUserMonster(@NonNull final Callback<UserMonsterResponse> callback) {
        Call<UserMonsterResponse> request = mClient.getUserMonsters();
        request.enqueue(callback);
        return request;
    }

    /**
     * API Interface which use the retrofit annotation
     */
    public interface ApiInterface {
        @GET(HttpEndPoint.USER_MONSTERS)
        Call<UserMonsterResponse> getUserMonsters();

        @GET(HttpEndPoint.USER_FRIENDS)
        Call<FriendsResponse> getFriends();

        @GET(HttpEndPoint.USER_LOGOUT)
        Call<LogoutResponse> logout();

        @POST(HttpEndPoint.USER_REGISTER)
        Call<RegisterResponse> register(@Body RegisterRequest body);

        @POST(HttpEndPoint.USER_SIGN_IN)
        Call<SignInResponse> signIn(@Body SignInRequest body);

        @POST(HttpEndPoint.USER_PASSWORD)
        Call<ForgotPasswordResponse> forgotPassword(@Body ForgotPasswordRequest body);

        @GET(HttpEndPoint.MONSTERS_LIST)
        Call<MonstersResponse> getMonsters();

        @GET(HttpEndPoint.USER_INFO)
        Call<UserResponse> getUserInfo();

        @GET(HttpEndPoint.PLACES_LIST)
        Call<PlacesResponse> getPlaces();

        @GET(HttpEndPoint.REGIONS_LIST)
        Call<RegionsResponse> getRegions();

        @GET
        Call<RegionResponse> getRegionDetail(@Url String endPoint);
    }
}
