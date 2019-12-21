package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

EditText etGitHubUser;
Button btnGetRepos;
TextView tvRepoList;
RequestQueue requestQueue;

String baseUrl = "https://api.github.com/users/";
String url;
    private Object ex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.etGitHubUser = (EditText) findViewById(R.id.et_github_user);
        this.btnGetRepos = (Button) findViewById(R.id.btn_get_repos);
        this.tvRepoList = (TextView) findViewById(R.id.tv_repo_list);
        this.tvRepoList.setMovementMethod(new ScrollingMovementMethod());

        requestQueue = Volley.newRequestQueue(this);
    }


    // definition of helper function to clear the list of repo
    private void clearRepoList(){
        this.tvRepoList.setText("");
    }

    //definitio of helper method to add a repository on the list
    private void addToRepoList(String repoName, String lastUpdated){
        //this will add a new repo to out list.
        //it combines repoName and lastUpdated strings together.
        //And then adds them followed by a new line (\n\n
        String strRow = repoName + "/" + lastUpdated;
        String currentText = tvRepoList.getText().toString();
        this.tvRepoList.setText(currentText+"\n\n"+strRow);
    }

    //
    private void setRepoListText(String str){
        //this is used to set the text of our repo list to a specific string
        //used to write "No repos found" if nothing is there

        this.tvRepoList.setText(str);
    }

    private void getRepoList(String username){
        //constructing the url for the request
        this.url = this.baseUrl + username + "/repos";
        JsonArrayRequest arrReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response){
                        //check if response is not empty, if empty no repos for this user
                        if(response.length() > 0){
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject jsonObj = response.getJSONObject(i);
                                    String repoName = jsonObj.get("name").toString();
                                    String lastUpdated = jsonObj.get("updated_at").toString();
                                    addToRepoList(repoName, lastUpdated);

                                }
                            }catch(JSONException ex){
                                    Log.e("Volley", "Invalid JSON Object");
                                }
                            }
                        else{
                            setRepoListText("No repos found");
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        setRepoListText("Error while calling REST API");
                        Log.e("Volley", error.toString());
                    }
                });
                requestQueue.add(arrReq);
    }

    public void getReposClicked(View v){
        //clear the repo list
        clearRepoList();
        //call getRepoList() method
        getRepoList(etGitHubUser.getText().toString());
    }
}
