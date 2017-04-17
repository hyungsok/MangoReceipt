package com.mangoreceipt.protocol;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by hyungsoklee on 2017. 4. 8..
 */

public interface ReceiptService {
    @Multipart
    @POST("/api/receipt/")
    Call<Receipt> postImage(@Part MultipartBody.Part file);
}
