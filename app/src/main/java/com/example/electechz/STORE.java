package com.example.electechz;

import android.graphics.Bitmap;
import android.view.View;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface STORE {
    String BASE_URL   = "http://192.168.43.242/ElecTechz/";
//    String BASE_URL   = "http://34.211.226.84/ElecTechz/";
    String SP         = "com.example.electechz.SP";

    HttpLoggingInterceptor.Level Level = HttpLoggingInterceptor.Level.BASIC;

    interface API_Client {
        @FormUrlEncoded
        @POST("login.php")
        Call<ResponseModel>
        login(@Field("type")     int type,
              @Field("email")    String email,
              @Field("password") String password);

        @FormUrlEncoded
        @POST("register.php")
        Call<ResponseModel>
        register(@Field("type")     int type,
                 @Field("email")    String email,
                 @Field("password") String password,
                 @Field("name")     String name,
                 @Field("address")  String address,
                 @Field("mobile")   String mobile);





        @FormUrlEncoded
        @POST("list_products.php")
        Call<ResponseModel_Product>
        list_products(@Field("category") String category);

        @FormUrlEncoded
        @POST("upload_product.php")
        Call<ResponseModel>
        uploadProduct(@Field("name")        String name,
                      @Field("description") String description,
                      @Field("category")    String category,
                      @Field("price")       String price,
                      @Field("quantity")    String quantity,
                      @Field("filename")    String filename);

        @FormUrlEncoded
        @POST("update_product.php")
        Call<ResponseModel>
        updateProduct(@Field("id")          String id,
                      @Field("name")        String name,
                      @Field("description") String description,
                      @Field("category")    String category,
                      @Field("price")       String price,
                      @Field("quantity")    String quantity,
                      @Field("filename")    String filename);

        @FormUrlEncoded
        @POST("delete_product.php")
        Call<ResponseModel>
        deleteProduct(@Field("id") String id);

        @Multipart
        @POST("image_upload.php")
        Call<ResponseModel>
        uploadImage(@Part MultipartBody.Part image, @Part("filename") RequestBody filename);





        @FormUrlEncoded
        @POST("list_vacancies.php")
        Call<ResponseModel_Vacancy>
        list_vacancies(@Field("category") String category);

        @FormUrlEncoded
        @POST("upload_vacancy.php")
        Call<ResponseModel>
        uploadVacancy(@Field("position") String position,
                      @Field("location") String location,
                      @Field("salary")   String salary,
                      @Field("details")  String details,
                      @Field("category") String category);

        @FormUrlEncoded
        @POST("update_vacancy.php")
        Call<ResponseModel>
        updateVacancy(@Field("id")       String id,
                      @Field("position") String position,
                      @Field("location") String location,
                      @Field("salary")   String salary,
                      @Field("details")  String details,
                      @Field("category") String category);

        @FormUrlEncoded
        @POST("delete_vacancy.php")
        Call<ResponseModel>
        deleteVacancy(@Field("id") String id);





        @FormUrlEncoded
        @POST("list_programs.php")
        Call<ResponseModel_Program>
        list_programs(@Field("category") String category);

        @FormUrlEncoded
        @POST("upload_program.php")
        Call<ResponseModel>
        uploadProgram(@Field("title")    String title,
                      @Field("location") String location,
                      @Field("date")     String date,
                      @Field("details")  String details,
                      @Field("category") String category);

        @FormUrlEncoded
        @POST("update_program.php")
        Call<ResponseModel>
        updateProgram(@Field("id")       String id,
                      @Field("title")    String title,
                      @Field("location") String location,
                      @Field("date")     String date,
                      @Field("details")  String details,
                      @Field("category") String category);

        @FormUrlEncoded
        @POST("delete_program.php")
        Call<ResponseModel>
        deleteProgram(@Field("id") String id);




        @FormUrlEncoded
        @POST("list_orders.php")
        Call<ResponseModel_Order>
        list_orders(@Field("email") String email);

        @FormUrlEncoded
        @POST("upload_order.php")
        Call<ResponseModel>
        uploadOrder(@Field("email")    String email,
                    @Field("cc")       String cc,
                    @Field("exp")      String exp,
                    @Field("cvv")      String cvv,
                    @Field("products") String products);

        @FormUrlEncoded
        @POST("update_order.php")
        Call<ResponseModel>
        updateOrder(@Field("id") String id);

        @FormUrlEncoded
        @POST("delete_order.php")
        Call<ResponseModel>
        deleteOrder(@Field("id") String id);





        @FormUrlEncoded
        @POST("list_apps.php")
        Call<ResponseModel_App>
        list_apps(@Field("mode")  String mode,
                  @Field("email") String email);

        @FormUrlEncoded
        @POST("upload_app.php")
        Call<ResponseModel>
        uploadApp(@Field("mode")       String mode,
                  @Field("email")      String email,
                  @Field("title")      String title,
                  @Field("location")   String location,
                  @Field("category")   String category,
                  @Field("salaryDate") String salaryDate,
                  @Field("filename")   String filename);

        @FormUrlEncoded
        @POST("update_app.php")
        Call<ResponseModel>
        updateApp(@Field("id") String id);

        @FormUrlEncoded
        @POST("delete_app.php")
        Call<ResponseModel>
        deleteApp(@Field("id") String id);

        @Multipart
        @POST("cv_upload.php")
        Call<ResponseModel>
        uploadCV(@Part MultipartBody.Part cv, @Part("filename") RequestBody filename);





        @FormUrlEncoded
        @POST("list_users.php")
        Call<ResponseModel_User>
        list_users(@Field("type") int type);

        @FormUrlEncoded
        @POST("update_user.php")
        Call<ResponseModel>
        updateUser(@Field("type")     int type,
                   @Field("email")    String email,
                   @Field("password") String password,
                   @Field("name")     String name,
                   @Field("address")  String address,
                   @Field("mobile")   String mobile);

        @FormUrlEncoded
        @POST("delete_user.php")
        Call<ResponseModel>
        deleteUser(@Field("type") int type,
                   @Field("id")   String id);




        @FormUrlEncoded
        @POST("upload_feedback.php")
        Call<ResponseModel>
        uploadFeedback(@Field("type")     int type,
                       @Field("email")    String email,
                       @Field("feedback") String feedback);
    }

    class ResponseModel {
        private int    status;
        private String message;
        private String name;
        private String address;
        private String mobile;
        private int    type;

        public int getStatus() {
            return status;
        }
        public String getMessage() {
            return message;
        }
        public String getName() {
            return name;
        }
        public String getAddress() {
            return address;
        }
        public String getMobile() {
            return mobile;
        }
        public int getType() {
            return type;
        }
    }

    class ResponseModel_Product {
        private int    status;
        private String message;
        private List<Product> products;

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public List<Product> getProducts() {
            return products;
        }
    }

    class ResponseModel_Vacancy {
        private int    status;
        private String message;
        private List<Vacancy> vacancies;

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public List<Vacancy> getVacancies() {
            return vacancies;
        }
    }

    class ResponseModel_Program {
        private int    status;
        private String message;
        private List<Program> programs;

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public List<Program> getPrograms() {
            return programs;
        }
    }

    class ResponseModel_Order {
        private int    status;
        private String message;
        private List<Order> orders;

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public List<Order> getOrders() {
            return orders;
        }
    }

    class ResponseModel_App {
        private int    status;
        private String message;
        private List<App> apps;

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public List<App> getApps() {
            return apps;
        }
    }

    class ResponseModel_User {
        private int    status;
        private String message;
        private List<User> users;

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public List<User> getUsers() {
            return users;
        }
    }

    class Product {
        String id;
        String name;
        String description;
        String category;
        String price;
        String quantity;
        String filename;
        Bitmap image;
        boolean cartmode;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getCategory() {
            return category;
        }

        public String getPrice() {
            return price;
        }

        public String getQuantity() {
            return quantity;
        }

        public String getFilename() {
            return filename;
        }

        public Bitmap getImage() {
            return image;
        }

        public boolean isCartmode() {
            return cartmode;
        }
    }

    class Vacancy {
        String id;
        String position;
        String location;
        String salary;
        String details;
        String category;

        public String getId() {
            return id;
        }

        public String getPosition() {
            return position;
        }

        public String getLocation() {
            return location;
        }

        public String getSalary() {
            return salary;
        }

        public String getDetails() {
            return details;
        }

        public String getCategory() {
            return category;
        }
    }

    class Program {
        String id;
        String title;
        String location;
        String date;
        String details;
        String category;

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getLocation() {
            return location;
        }

        public String getDate() {
            return date;
        }

        public String getDetails() {
            return details;
        }

        public String getCategory() {
            return category;
        }
    }

    class Order {
        String id;
        String status;
        String email;
        String cc;
        String exp;
        String cvv;
        String products;

        public String getId() {
            return id;
        }

        public String getStatus() {
            return status;
        }

        public String getEmail() {
            return email;
        }

        public String getCc() {
            return cc;
        }

        public String getExp() {
            return exp;
        }

        public String getCvv() {
            return cvv;
        }

        public String getProducts() {
            return products;
        }
    }

    class App {
        String id;
        String mode;
        String status;
        String email;
        String title;
        String category;
        String location;
        String salaryDate;
        String CV;

        public String getId() {
            return id;
        }

        public String getMode() {
            return mode;
        }

        public String getStatus() {
            return status;
        }

        public String getEmail() {
            return email;
        }

        public String getTitle() {
            return title;
        }

        public String getCategory() {
            return category;
        }

        public String getLocation() {
            return location;
        }

        public String getSalaryDate() {
            return salaryDate;
        }

        public String getCV() {
            return CV;
        }
    }

    class User {
        String id;
        String email;
        String name;
        String address;
        String mobile;

        public String getId() {
            return id;
        }

        public String getEmail() {
            return email;
        }

        public String getName() {
            return name;
        }

        public String getAddress() {
            return address;
        }

        public String getMobile() {
            return mobile;
        }
    }
}