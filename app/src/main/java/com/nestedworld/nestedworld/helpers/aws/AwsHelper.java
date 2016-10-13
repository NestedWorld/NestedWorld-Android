package com.nestedworld.nestedworld.helpers.aws;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;

public final class AwsHelper {
    private AwsHelper() {
        //Private constructor for avoiding this class to be construct
    }

    public static void upload(@NonNull final Context context, @NonNull final File file) {
        //TODO use good credential
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context.getApplicationContext(),
                "COGNITO_IDENTITY_POOL",
                Regions.EU_CENTRAL_1
        );

        //TODO check the doc
        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
        TransferUtility transferUtility = new TransferUtility(s3, context);
        transferUtility.upload(null, "picture", file);
    }
}
