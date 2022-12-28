package com.example.backgroundsync.network;

import com.example.backgroundsync.model.PostModel;

import java.util.List;
import io.reactivex.Single;
import retrofit2.http.GET;

public interface ServiceInterface {

    @GET("posts")
    Single<List<PostModel>> getPosts();
 }
