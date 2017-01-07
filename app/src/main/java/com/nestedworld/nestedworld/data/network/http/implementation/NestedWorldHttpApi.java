package com.nestedworld.nestedworld.data.network.http.implementation;

import android.support.annotation.NonNull;

import com.google.gson.GsonBuilder;
import com.nestedworld.nestedworld.data.database.models.session.Session;
import com.nestedworld.nestedworld.data.network.http.models.request.friends.AddFriendRequest;
import com.nestedworld.nestedworld.data.network.http.models.request.users.auth.ForgotPasswordRequest;
import com.nestedworld.nestedworld.data.network.http.models.request.users.auth.RegisterRequest;
import com.nestedworld.nestedworld.data.network.http.models.request.users.auth.SignInRequest;
import com.nestedworld.nestedworld.data.network.http.models.response.attacks.AttacksResponse;
import com.nestedworld.nestedworld.data.network.http.models.response.friends.AddFriendResponse;
import com.nestedworld.nestedworld.data.network.http.models.response.geo.portal.PortalsResponse;
import com.nestedworld.nestedworld.data.network.http.models.response.monsters.MonsterAttackResponse;
import com.nestedworld.nestedworld.data.network.http.models.response.monsters.MonstersResponse;
import com.nestedworld.nestedworld.data.network.http.models.response.objects.ObjectsResponse;
import com.nestedworld.nestedworld.data.network.http.models.response.users.UserResponse;
import com.nestedworld.nestedworld.data.network.http.models.response.users.auth.ForgotPasswordResponse;
import com.nestedworld.nestedworld.data.network.http.models.response.users.auth.LogoutResponse;
import com.nestedworld.nestedworld.data.network.http.models.response.users.auth.RegisterResponse;
import com.nestedworld.nestedworld.data.network.http.models.response.users.auth.SignInResponse;
import com.nestedworld.nestedworld.data.network.http.models.response.users.friend.FriendsResponse;
import com.nestedworld.nestedworld.data.network.http.models.response.users.inventory.UserInventoryResponse;
import com.nestedworld.nestedworld.data.network.http.models.response.users.monster.UserMonsterResponse;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.helpers.session.SessionHelper;

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
    private final static String TAG = NestedWorldHttpApi.class.getSimpleName();

    private static NestedWorldHttpApi mSingleton;
    private NestedWorldApiInterface mClient;

    /*
     * #############################################################################################
     * # Constructor
     * #############################################################################################
     */
    private NestedWorldHttpApi() {
        if (mSingleton != null) {
            return;
        }

        //init API
        init();
    }

    /*
     * #############################################################################################
     * # Public (static) method
     * #############################################################################################
     */

    /**
     * Singleton
     *
     * @return singleton instance
     */
    @NonNull
    public static NestedWorldHttpApi getInstance() {
        if (mSingleton == null) {
            mSingleton = new NestedWorldHttpApi();
        }
        return mSingleton;
    }

    /**
     * Avoid leak (clear singleton)
     */
    public static void reset() {
        mSingleton = null;
    }

    /*
     * #############################################################################################
     * # Internal method
     * #############################################################################################
     */
    private void init() {
        LogHelper.d(TAG, "Init API(http) (end_point = " + HttpEndPoint.BASE_END_POINT + ")");

        // Define a request interceptor for displaying some log
        final HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Define a request interception for adding some custom headers
        final Interceptor httpHeaderInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                final Request.Builder requestBuilder = chain.request().newBuilder();

                final Session session = SessionHelper.getSession();
                if (session != null) {
                    requestBuilder.addHeader("X-User-Email", session.email);
                    requestBuilder.addHeader("Authorization", session.authToken);
                }

                return chain.proceed(requestBuilder.build());
            }
        };

        // We declare the client and we add our interceptors …
        final OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(httpHeaderInterceptor)
                .addInterceptor(httpLoggingInterceptor)// we add logging interceptor as last httpHeaderInterceptor
                .build();

        // Create the converterFactory
        final GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        final GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(builder.create());

        // Init retrofit
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpEndPoint.BASE_END_POINT)
                .addConverterFactory(gsonConverterFactory)
                .client(client)
                .build();

        mClient = retrofit.create(NestedWorldApiInterface.class);
    }

    /*
     * #############################################################################################
     * # NestedWorldHttpApi abstraction
     * #############################################################################################
     */
    @NonNull
    public Call<RegisterResponse> register(@NonNull final String email,
                                           @NonNull final String password,
                                           @NonNull final String pseudo) {
        return mClient.register(new RegisterRequest(email, password, pseudo));
    }

    @NonNull
    public Call<SignInResponse> signIn(@NonNull final String email,
                                       @NonNull final String password) {
        final String appToken = "test";
        return mClient.signIn(new SignInRequest(email, password, appToken));
    }

    @NonNull
    public Call<ForgotPasswordResponse> forgotPassword(@NonNull final String email) {
        return mClient.forgotPassword(new ForgotPasswordRequest(email));
    }

    @NonNull
    public Call<AddFriendResponse> addFriend(@NonNull final String pseudo) {
        return mClient.addFriends(new AddFriendRequest(pseudo));
    }

    @NonNull
    public Call<MonsterAttackResponse> getMonsterAttack(final long monsterId) {
        return mClient.getMonsterAttack(monsterId);
    }

    @NonNull
    public Call<AttacksResponse> getAttacks() {
        return mClient.getAttacks();
    }

    @NonNull
    public Call<LogoutResponse> logout() {
        return mClient.logout();
    }

    @NonNull
    public Call<MonstersResponse> getMonsters() {
        return mClient.getMonsters();
    }

    @NonNull
    public Call<UserResponse> getUserInfo() {
        return mClient.getUserInfo();
    }

    @NonNull
    public Call<FriendsResponse> getFriends() {
        return mClient.getFriends();
    }

    @NonNull
    public Call<UserMonsterResponse> getUserMonster() {
        return mClient.getUserMonsters();
    }

    @NonNull
    public Call<UserInventoryResponse> getUserInventory() {
        return mClient.getUserInventory();
    }

    @NonNull
    public Call<ObjectsResponse> getShopItems() {
        return mClient.getObjects();
    }

    @NonNull
    public Call<PortalsResponse> getPortals(final double latitude,
                                            final double longitude) {
        return mClient.getPortals(latitude, longitude);
    }
}