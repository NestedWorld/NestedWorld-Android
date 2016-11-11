package com.nestedworld.nestedworld.network.http.implementation;

import android.support.annotation.NonNull;

import com.google.gson.GsonBuilder;
import com.nestedworld.nestedworld.database.models.Session;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.helpers.session.SessionHelper;
import com.nestedworld.nestedworld.network.http.models.request.friends.AddFriendRequest;
import com.nestedworld.nestedworld.network.http.models.request.users.auth.ForgotPasswordRequest;
import com.nestedworld.nestedworld.network.http.models.request.users.auth.RegisterRequest;
import com.nestedworld.nestedworld.network.http.models.request.users.auth.SignInRequest;
import com.nestedworld.nestedworld.network.http.models.response.attack.AttacksResponse;
import com.nestedworld.nestedworld.network.http.models.response.friend.AddFriendResponse;
import com.nestedworld.nestedworld.network.http.models.response.geo.portal.PortalsResponse;
import com.nestedworld.nestedworld.network.http.models.response.monsters.MonsterAttackResponse;
import com.nestedworld.nestedworld.network.http.models.response.monsters.MonstersResponse;
import com.nestedworld.nestedworld.network.http.models.response.object.ObjectsResponse;
import com.nestedworld.nestedworld.network.http.models.response.geo.regions.RegionsResponse;
import com.nestedworld.nestedworld.network.http.models.response.users.UserResponse;
import com.nestedworld.nestedworld.network.http.models.response.users.auth.ForgotPasswordResponse;
import com.nestedworld.nestedworld.network.http.models.response.users.auth.LogoutResponse;
import com.nestedworld.nestedworld.network.http.models.response.users.auth.RegisterResponse;
import com.nestedworld.nestedworld.network.http.models.response.users.auth.SignInResponse;
import com.nestedworld.nestedworld.network.http.models.response.users.friend.FriendsResponse;
import com.nestedworld.nestedworld.network.http.models.response.users.inventory.UserInventoryResponse;
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

/**
 * Simple Api containing the retrofit interface and his implementation
 */
public final class NestedWorldHttpApi {
    private static NestedWorldHttpApi mSingleton;
    private final String TAG = getClass().getSimpleName();
    private NestedWorldApiInterface mClient;
    private Call<PortalsResponse> portals;

    /*
    ** Constructor
     */
    private NestedWorldHttpApi() {
        if (mSingleton != null) {
            return;
        }

        //init API
        init();
    }

    /*
    ** Singleton
     */
    public static NestedWorldHttpApi getInstance() {
        if (mSingleton == null) {
            mSingleton = new NestedWorldHttpApi();
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
    private void init() {
        LogHelper.d(TAG, "Init API(http) (end_point = " + HttpEndPoint.BASE_END_POINT + ")");

        // Define a request interceptor for displaying some log
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Define a request interception for adding some custom headers
        Interceptor httpHeaderInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request.Builder requestBuilder = chain.request().newBuilder();

                Session session = SessionHelper.getSession();
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

        mClient = retrofit.create(NestedWorldApiInterface.class);
    }

    /**
     * Interface implementation
     */
    public Call<RegisterResponse> register(@NonNull final String email, @NonNull final String password, @NonNull final String pseudo) {
        return mClient.register(new RegisterRequest(email, password, pseudo));
    }

    public Call<SignInResponse> signIn(@NonNull final String email, @NonNull final String password) {
        String appToken = "test";
        return mClient.signIn(new SignInRequest(email, password, appToken));
    }

    public Call<ForgotPasswordResponse> forgotPassword(@NonNull final String email) {
        return mClient.forgotPassword(new ForgotPasswordRequest(email));
    }

    public Call<AddFriendResponse> addFriend(@NonNull final String pseudo) {
        return mClient.addFriends(new AddFriendRequest(pseudo));
    }

    public Call<MonsterAttackResponse> getMonsterAttack(final long monsterId) {
        return mClient.getMonsterAttack(monsterId);
    }

    public Call<AttacksResponse> getAttacks() {
        return mClient.getAttacks();
    }

    public Call<LogoutResponse> logout() {
        return mClient.logout();
    }

    public Call<MonstersResponse> getMonsters() {
        return mClient.getMonsters();
    }

    public Call<UserResponse> getUserInfo() {
        return mClient.getUserInfo();
    }

    public Call<RegionsResponse> getRegions() {
        return mClient.getRegions();
    }

    public Call<FriendsResponse> getFriends() {
        return mClient.getFriends();
    }

    public Call<UserMonsterResponse> getUserMonster() {
        return mClient.getUserMonsters();
    }

    public Call<UserInventoryResponse> getUserInventory() {
        return mClient.getUserInventory();
    }

    public Call<ObjectsResponse> getShopItems() {
        return mClient.getObjects();
    }

    public Call<PortalsResponse> getPortals() {
        return mClient.getPortals();
    }
}