package nickkelly.dndcompanion;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SpellActivity extends AppCompatActivity {

	private EditText spellText;
	private Button searchButton;
	private String spellName;
	private String baseURL = "https://dnd5eapi.co";
	private String apiURL = "https://dnd5eapi.co/api/spells/";
	private TextView descBox, higherLevelBox, extraInfoBox;
	private TextView descLabel, higherLevelLabel, extraInfoLabel;
	private RequestQueue queue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spell);


		spellText = findViewById(R.id.spellName);
		searchButton = findViewById(R.id.searchButton);
		descBox = findViewById(R.id.descriptionBox);
		higherLevelBox = findViewById(R.id.higherLevelBox);
		extraInfoBox = findViewById(R.id.extraInfoBox);
		descLabel = findViewById(R.id.descLabel);
		higherLevelLabel = findViewById(R.id.higherLevelLabel);
		extraInfoLabel = findViewById(R.id.extraInfoLabel);

		queue = Volley.newRequestQueue(this);

		searchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				descBox.setText("");
				descBox.setMovementMethod(new ScrollingMovementMethod());
				higherLevelBox.setText("");
				extraInfoBox.setText("");
				descLabel.setText("");
				higherLevelLabel.setText("");
				extraInfoLabel.setText("");
				if (spellText.getText().length() == 0) {
					spellText.setHint("ENTER A SPELL HERE");
				}
				else {
					spellName = spellText.getText().toString().trim().toLowerCase();

					String[] sArray;
					sArray = spellName.split("\\s+");

					String urlString = "";
					for (int i = 0; i < sArray.length; i++) {
						urlString += sArray[i] + "-";
					}
					urlString = urlString.substring(0, urlString.length() - 1);
					urlString = apiURL + urlString;
					System.out.println(urlString);
					JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, urlString, null, new Response.Listener<JSONObject>() {
						public void onResponse(JSONObject response) {
							try {
								descLabel.setText("Description: ");

								String descString = response.getString("desc").substring(2, response.getString("desc").length() - 2);
								descString = descString.replace("\",\"", "\n\n");
								descString = descString.replace("â€™", "'");
								descBox.setText(descString);

								if (response.has("higher_level")) {
									higherLevelLabel.setText("Higher Level: ");
									higherLevelBox.setText(response.getString("higher_level").substring(2, response.getString("higher_level").length() - 2));
								}
								String extraInfo = "";
								if (response.has("range")) {
									extraInfo = "Range: " + response.getString("range") + "\n";
								}
								if (response.has("duration")) {
									extraInfo += "Duration: " + response.getString("duration") + "\n";
								}
								if (response.has("concentration")) {
									extraInfo += "Concentration: " + response.getString("concentration");
								}

								extraInfoLabel.setText("Extra Info: ");
								extraInfoBox.setText(extraInfo);

							}
							catch (JSONException e) {
								descBox.setText("error321");
							}
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							descBox.setText("Spell not found.");
						}
					});
					queue.add(jsonRequest);
				}
			}
		});

	}


	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			View v = getCurrentFocus();
			if (v instanceof EditText) {
				Rect outRect = new Rect();
				v.getGlobalVisibleRect(outRect);
				if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
					v.clearFocus();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
			}
		}
		return super.dispatchTouchEvent(event);
	}
}
