package com.nestedworld.nestedworld.api.http.implementation;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.GsonBuilder;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.api.http.models.request.users.UpdateUserRequest;
import com.nestedworld.nestedworld.api.http.models.request.users.auth.ForgotPasswordRequest;
import com.nestedworld.nestedworld.api.http.models.request.users.auth.RegisterRequest;
import com.nestedworld.nestedworld.api.http.models.request.users.auth.SignInRequest;
import com.nestedworld.nestedworld.api.http.models.response.monsters.MonstersResponse;
import com.nestedworld.nestedworld.api.http.models.response.places.PlacesResponse;
import com.nestedworld.nestedworld.api.http.models.response.places.regions.RegionResponse;
import com.nestedworld.nestedworld.api.http.models.response.places.regions.RegionsResponse;
import com.nestedworld.nestedworld.api.http.models.response.users.UserResponse;
import com.nestedworld.nestedworld.api.http.models.response.users.auth.ForgotPasswordResponse;
import com.nestedworld.nestedworld.api.http.models.response.users.auth.LogoutResponse;
import com.nestedworld.nestedworld.api.http.models.response.users.auth.RegisterResponse;
import com.nestedworld.nestedworld.api.http.models.response.users.auth.SignInResponse;
import com.nestedworld.nestedworld.api.http.models.response.users.friend.FriendsResponse;
import com.nestedworld.nestedworld.api.http.models.response.users.monster.UserMonsterResponse;
import com.nestedworld.nestedworld.authenticator.UserManager;
import com.nestedworld.nestedworld.helper.log.LogHelper;
import com.nestedworld.nestedworld.models.Friend;
import com.nestedworld.nestedworld.models.Region;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Url;

/**
 * Simple Api containing the retrofit interface and his implementation
 */
public class NestedWorldHttpApi {
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
                Request request = chain.request().newBuilder()
                        .addHeader("X-User-Email", "" + UserManager.get(mContext).getCurrentAccountName())
                        .addHeader("Authorization", "Bearer " + UserManager.get(mContext).getCurrentAuthToken(mContext))
                        .build();
                return chain.proceed(request);
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
    public void register(@NonNull final String email, @NonNull final String password, @NonNull final String pseudo, @NonNull final Callback<RegisterResponse> callback) {
        mClient.register(new RegisterRequest(email, password, pseudo)).enqueue(callback);
    }

    public void signIn(@NonNull final String email, @NonNull final String password, @NonNull final Callback<SignInResponse> callback) {
        String app_token = mContext.getString(R.string.app_token);
        mClient.signIn(new SignInRequest(email, password, app_token)).enqueue(callback);
    }

    public void forgotPassword(@NonNull final String email, @NonNull final Callback<ForgotPasswordResponse> callback) {
        mClient.forgotPassword(new ForgotPasswordRequest(email)).enqueue(callback);
    }

    public void logout(@NonNull final Callback<LogoutResponse> callback) {
        mClient.logout().enqueue(callback);
    }

    public void getMonsters(@NonNull final Callback<MonstersResponse> callback) {
        mClient.getMonsters().enqueue(callback);
    }

    public void getUserInfo(@NonNull final Callback<UserResponse> callback) {
        mClient.getUserInfo().enqueue(callback);
    }

    public void getPlaces(@NonNull Callback<PlacesResponse> callback) {
        mClient.getPlaces().enqueue(callback);
    }

    public void getRegions(@NonNull final Callback<RegionsResponse> callback) {
        mClient.getRegions().enqueue(callback);
    }

    public void getRegionDetails(@NonNull final Region region, @NonNull final Callback<RegionResponse> callback) {
        mClient.getRegionDetail(region.url).enqueue(callback);
    }

    public void getFriends(@NonNull final Callback<FriendsResponse> callback) {
        mClient.getFriends().enqueue(callback);
    }

    public void getUserMonster(@NonNull final Callback<UserMonsterResponse> callback) {
        mClient.getUserMonsters().enqueue(callback);
    }

    public void updateUserInformation(@NonNull final UpdateUserRequest userInfo, @NonNull final Callback<UserResponse> callback) {
        mClient.updateUserInfo(userInfo).enqueue(callback);
    }

    /**
     * API Interface which use the retrofit annotation
     */
    public interface ApiInterface {
        @GET(HttpEndPoint.USER_MONSTERS)
        Call<UserMonsterResponse> getUserMonsters();

        @GET(HttpEndPoint.USER_FRIENDS)
        Call<FriendsResponse> getFriends();

        @PUT(HttpEndPoint.USER_FRIENDS)
        Call<FriendsResponse> addFriend(@Body Friend body);

        @PUT(HttpEndPoint.USER_INFO)
        Call<UserResponse> updateUserInfo(@Body UpdateUserRequest body);

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
