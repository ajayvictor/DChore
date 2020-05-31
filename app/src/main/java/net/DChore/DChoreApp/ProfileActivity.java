package net.DChore.DChoreApp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    TextView textViewUsername, textViewCategory;
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<DataModel> data;
    static View.OnClickListener myOnClickListener;
    private static ArrayList<Integer> removedItems;
    public static String date, time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //if the user is not logged in
        //starting the login activity
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }


        textViewUsername = (TextView) findViewById(R.id.textViewUsername);
        textViewCategory = (TextView) findViewById(R.id.categoryType);

        //getting the current user
        User user = SharedPrefManager.getInstance(this).getUser();

        //setting the values to the textviews
        textViewUsername.setText(user.getUsername());
        textViewCategory.setText(user.getCategory());




        //when the user presses logout button
        //calling the logout method
        findViewById(R.id.buttonLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                SharedPrefManager.getInstance(getApplicationContext()).logout();
            }
        });



        myOnClickListener = new MyOnClickListener(this);

        if(user.getCategory().equals("User")) {

            recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
            recyclerView.setHasFixedSize(true);

            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());


            user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
            final String username = user.getUsername();




            class WorkerDatas extends AsyncTask<Void, Void, String> {
                ProgressBar progressBar;


                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    progressBar = (ProgressBar) findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.VISIBLE);

                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    progressBar = (ProgressBar) findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.GONE);

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(s);

                        //if no error in response
                        if (obj.getBoolean("status")) {
                            Log.d("Status Message",obj.getString("status_message"));

                            Log.d("Status Message",obj.getString("workers"));

                            JSONArray jsonArray = new JSONArray(obj.getString("workers"));

                            data = new ArrayList<DataModel>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject first = jsonArray.getJSONObject(i);
                                data.add(new DataModel(
                                        first.getString("name"),
                                        first.getString("job"),
                                        first.getInt("age"),
                                        first.getString("place"),
                                        first.getString("mobile"),
                                        first.getDouble("experience")
                                ));
                            }

                            //0 for just retrieving first object you can loop it

                            //getting the user from the response
                            //JSONObject userJson = obj.getJSONObject("workers");

                            adapter = new CustomAdapter(data);
                            recyclerView.setAdapter(adapter);


                        } else {
                            Log.d("Status Message",obj.getString("status_message"));

                            //Toast.makeText(context.getApplicationContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Error occurred while connecting to the server", Toast.LENGTH_SHORT).show();

                        e.printStackTrace();
                    }
                }

                @Override
                protected String doInBackground(Void... voids) {
                    //creating request handler object
                    RequestHandler requestHandler = new RequestHandler();

                    //creating request parameters
                    HashMap<String, String> params = new HashMap<>();
                    params.put("username", username);
                    System.out.println("Execution Happening");

                    //returing the response
                    return requestHandler.sendPostRequest(URLs.URL_WORKER_DATA, params);
                }
            }
            WorkerDatas workerDatas = new WorkerDatas();
            workerDatas.execute();
//            try {
//                Thread.sleep(10000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            Log.d("Message","Execution Completed");


        }
        else{

        }



    }



    private class MyOnClickListener implements View.OnClickListener {

        private final Context context;

        private MyOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {

            int selectedItemPosition = recyclerView.getChildPosition(v);
            RecyclerView.ViewHolder viewHolder
                    = recyclerView.findViewHolderForPosition(selectedItemPosition);
            TextView textViewName
                    = (TextView) viewHolder.itemView.findViewById(R.id.textViewName);
            TextView textViewMobile
                    = (TextView) viewHolder.itemView.findViewById(R.id.textViewMobile);
            String selectedName = (String) textViewName.getText();
            final String selectedUsername = (String) textViewName.getText();
            final String selectedMobile = (String) textViewMobile.getText();



            Log.d("Time","Time and Date" + time + date);

            if(time != null && date != null)
            {


            AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
            builder.setTitle("Confirm Worker!");
            builder.setMessage("You are about to book this worker named as " + selectedName + ", Do you want to confirm?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    User user;
                    user = SharedPrefManager.getInstance(context.getApplicationContext()).getUser();
                    final String username = user.getUsername();



                    class WorkerConfirm extends AsyncTask<Void, Void, String> {
                        ProgressBar progressBar;


                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            progressBar = (ProgressBar) findViewById(R.id.progressBar);
                            progressBar.setVisibility(View.VISIBLE);

                        }

                        @Override
                        protected void onPostExecute(String s) {
                            super.onPostExecute(s);
                            progressBar = (ProgressBar) findViewById(R.id.progressBar);
                            progressBar.setVisibility(View.GONE);

                            try {
                                //converting response to json object
                                JSONObject obj = new JSONObject(s);

                                //if no error in response
                                if (obj.getBoolean("status")) {
                                    Log.d("Status Message",obj.getString("status_message"));

                                    Toast.makeText(context.getApplicationContext(), obj.getString("status_message"), Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(context.getApplicationContext(), obj.getString("status_message"), Toast.LENGTH_SHORT).show();


                                    //Toast.makeText(context.getApplicationContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(context.getApplicationContext(), "Error occurred while connecting to the server", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }

                        @Override
                        protected String doInBackground(Void... voids) {
                            //creating request handler object
                            RequestHandler requestHandler = new RequestHandler();

                            //creating request parameters
                            HashMap<String, String> params = new HashMap<>();
                            params.put("username", username);
                            params.put("worker_mobile", selectedMobile);
                            params.put("date", date);
                            params.put("time", time);
                            System.out.println("Execution Happening");

                            //returing the response
                            return requestHandler.sendPostRequest(URLs.URL_WORKER_CONFIRM, params);
                        }
                    }
                    new WorkerConfirm().execute();

                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(context.getApplicationContext(), "You've changed your mind to book this worker!! Feel free to book again, Thanks!", Toast.LENGTH_SHORT).show();
                }
            });

            builder.show();

            Log.d("number", String.valueOf(selectedName) + ", Name: " +selectedName + "Date: " + date + "Time: " + time);
        }
            else
            {
                Toast.makeText(context.getApplicationContext(), "Please select both date and time!!", Toast.LENGTH_SHORT).show();

            }
        }





        /*
        private void removeItem(View v) {
            int selectedItemPosition = recyclerView.getChildPosition(v);
            RecyclerView.ViewHolder viewHolder
                    = recyclerView.findViewHolderForPosition(selectedItemPosition);
            TextView textViewName
                    = (TextView) viewHolder.itemView.findViewById(R.id.textViewName);
            String selectedName = (String) textViewName.getText();
            int selectedItemId = -1;
            for (int i = 0; i < WorkersData.nameArray.length; i++) {
                if (selectedName.equals(WorkersData.nameArray[i])) {
                    selectedItemId = WorkersData.mobileArray[i];
                }
            }
            removedItems.add(selectedItemId);
            data.remove(selectedItemPosition);
            adapter.notifyItemRemoved(selectedItemPosition);
        }
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        Log.d("check", "clicked!!");
        User user;

        TextView addItem = findViewById(R.id.add_item);

        addItem.setText("Home");

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (ProfileActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });


        user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        final String username = user.getUsername();

        TextView textViewBook = findViewById(R.id.textViewBook);

        textViewBook.setText("Please find your bookings below!!");


        class BookedWorkerDatas extends AsyncTask<Void, Void, String> {
            ProgressBar progressBar;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.setVisibility(View.GONE);

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (obj.getBoolean("status")) {
                        Log.d("Status Message",obj.getString("status_message"));

                        JSONArray jsonArray = new JSONArray(obj.getString("workers"));

                        ArrayList<BookedDataModel> data = new ArrayList<BookedDataModel>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject first = jsonArray.getJSONObject(i);
                            data.add(new BookedDataModel(
                                    first.getString("name"),
                                    first.getString("job"),
                                    first.getInt("age"),
                                    first.getString("place"),
                                    first.getInt("mobile"),
                                    first.getDouble("experience"),
                                    first.getString("date"),
                                    first.getString("time"),
                                    first.getString("status")
                            ));
                        }

                        //0 for just retrieving first object you can loop it

                        //getting the user from the response
                        //JSONObject userJson = obj.getJSONObject("workers");

                        adapter = new BookedCustomAdapter(data);
                        recyclerView.setAdapter(adapter);


                    } else {
                        Log.d("Status Message","Error occurred");

                        Toast.makeText(getApplicationContext(), obj.getString("status_message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error occurred while connecting to the server", Toast.LENGTH_SHORT).show();

                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);
                System.out.println("Execution Happening");

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_BOOKED_DATA, params);
            }
        }
        BookedWorkerDatas bookedWorkerDatas = new BookedWorkerDatas();
        bookedWorkerDatas.execute();


        return true;
    }

    /*

    private void addRemovedItemToList() {
        int addItemAtListPosition = 3;
        data.add(addItemAtListPosition, new DataModel(
                WorkersData.nameArray[removedItems.get(0)],
                WorkersData.versionArray[removedItems.get(0)],
                WorkersData.id_[removedItems.get(0)],
                WorkersData.drawableArray[removedItems.get(0)]
        ));
        adapter.notifyItemInserted(addItemAtListPosition);
        removedItems.remove(0);
    }
    */

}