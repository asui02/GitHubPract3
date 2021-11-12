package com.nikochan76.githubpract3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.IOException;

import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    private ProgressBar mProgressBar;
    private TextView mTextView;
    private TextView mTextView2;
    private TextView UserNameGet;
    private TextView nameget;
    private TextView reposget;
    private TextView UserInfoGetName;
    private TextView GitUserInfoTxt;
    private TextView SearchUser;
    private TextView SearchUserTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.reposinftxt);
        mTextView2 = (TextView) findViewById(R.id.reposUsersTxt);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
        UserNameGet = (TextView) findViewById(R.id.gitusername);
        nameget = (TextView) findViewById(R.id.name);
        reposget = (TextView) findViewById(R.id.repos);
        UserInfoGetName = (TextView) findViewById(R.id.gituserinfo);
        GitUserInfoTxt = (TextView) findViewById(R.id.gituserinfotxt);
        SearchUser = (TextView) findViewById(R.id.searchuser);
        SearchUserTxt = (TextView) findViewById(R.id.searchusertxt);

    }

    public void onClick(View view) {
        mProgressBar.setVisibility(View.VISIBLE);

        GitHubService gitHubService = GitHubService.retrofit.create(GitHubService.class);

        final Call<List<Repos>> call = gitHubService.getRepos( UserNameGet.getText().toString());

        call.enqueue(new Callback<List<Repos>>() {
                         @Override
                         public void onResponse(Call<List<Repos>> call, Response<List<Repos>> response) {
                             // response.isSuccessfull() is true if the response code is 2xx
                             if (response.isSuccessful()) {
                                 // Выводим массив имён
                                 mTextView.setText(response.body().toString() + "\n");
                                 for (int i = 0; i < response.body().size(); i++) {
                                     // Выводим имена по отдельности
                                     mTextView.append(response.body().get(i).getName() + "\n");
                                 }

                                 mProgressBar.setVisibility(View.INVISIBLE);
                             } else {
                                 int statusCode = response.code();
                                 // Обрабатываем ошибку
                                 ResponseBody errorBody = response.errorBody();
                                 try {
                                     mTextView.setText(errorBody.string());
                                     mProgressBar.setVisibility(View.INVISIBLE);
                                 } catch (IOException e) {
                                     e.printStackTrace();
                                 }
                             }
                         }

                         @Override
                         public void onFailure(Call<List<Repos>> call, Throwable throwable) {
                             mTextView.setText("Что-то пошло не так: " + throwable.getMessage());
                         }
                     }
        );
    }
    public void onClickReposUsers(View view) {
        mProgressBar.setVisibility(View.VISIBLE);
        GitHubService gitHubService = GitHubService.retrofit.create(GitHubService.class);
        final Call<List<Contributor>> call =
                gitHubService.repoContributors(nameget.getText().toString(), reposget.getText().toString());

        call.enqueue(new Callback<List<Contributor>>() {
            @Override
            public void onResponse(Call<List<Contributor>> call, Response<List<Contributor>> response) {
                mTextView2.setText(response.body().toString());
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<List<Contributor>> call, Throwable throwable) {
                mTextView2.setText("Что-то пошло не так: " + throwable.getMessage());
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
    public void onClickGetUserInfo(View view) {
        mProgressBar.setVisibility(View.VISIBLE);

        GitHubService gitHubService = GitHubService.retrofit.create(GitHubService.class);
        final Call<User> call =
                gitHubService.getUser(UserInfoGetName.getText().toString());

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                // response.isSuccessfull() is true if the response code is 2xx
                if (response.isSuccessful()) {
                    User user = response.body();

                    // Получаем json из github-сервера и конвертируем его в удобный вид
                    GitUserInfoTxt.setText("Аккаунт Github: " + user.getName() +
                            "\nСайт: " + user.getBlog() +
                            "\nКомпания: " + user.getCompany());

                    mProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    int statusCode = response.code();

                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();
                    try {
                        GitUserInfoTxt.setText(errorBody.string());
                        mProgressBar.setVisibility(View.INVISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable throwable) {
                mTextView.setText("Что-то пошло не так: " + throwable.getMessage());
            }
        });
    }
    public void onClickSearch(View view) {
        mProgressBar.setVisibility(View.VISIBLE);

        GitHubService gitHubService = GitHubService.retrofit.create(GitHubService.class);
        // часть слова
        final Call<GitResult> call =
                gitHubService.getUsers(SearchUser.getText().toString());

        call.enqueue(new Callback<GitResult>() {
            @Override
            public void onResponse(Call<GitResult> call, Response<GitResult> response) {
                // response.isSuccessful() is true if the response code is 2xx
                if (response.isSuccessful()) {
                    GitResult result = response.body();

                    // Получаем json из github-сервера и конвертируем его в удобный вид
                    // Покажем только первого пользователя
                    SearchUserTxt.setText("");
                    for (int i= 0; i < 5; i++){
                        String user = "Аккаунт Github: " + result.getItems().get(i).getLogin();
                        SearchUserTxt.setText(SearchUserTxt.getText().toString() + "\n" + user);
                    }
                    Log.i("Git", String.valueOf(result.getItems().size()));

                    mProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    int statusCode = response.code();

                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();
                    try {
                        SearchUserTxt.setText(errorBody.string());
                        mProgressBar.setVisibility(View.INVISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<GitResult> call, Throwable throwable) {
                SearchUserTxt.setText("Что-то пошло не так: " + throwable.getMessage());
            }
        });
    }

}