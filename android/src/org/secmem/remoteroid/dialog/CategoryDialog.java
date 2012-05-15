package org.secmem.remoteroid.dialog;

import java.util.ArrayList;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.adapter.CategoryAdapter;
import org.secmem.remoteroid.database.CategoryDatabase;
import org.secmem.remoteroid.util.HongUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class CategoryDialog extends Activity implements OnClickListener, OnItemClickListener {
	
	private ListView listview;
	private CategoryAdapter adapter;
	private Button addBtn;
	private Button closeBtn;
	
	private CategoryDatabase database = new CategoryDatabase(this);
	private ArrayList<String> categoryList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_category);
		
		addBtn = (Button)findViewById(R.id.dialog_category_btn_add);
		closeBtn = (Button)findViewById(R.id.dialog_category_btn_close);
		
		addBtn.setOnClickListener(this);
		closeBtn.setOnClickListener(this);
		listview = (ListView)findViewById(R.id.dialog_category_list);
		listview.setOnItemClickListener(this);
		categoryList = getCategory();
		
		adapter = new CategoryAdapter(CategoryDialog.this, categoryList, R.layout.list_category);
		listview.setAdapter(adapter);
		
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		
		case R.id.dialog_category_btn_add:
			
			showInputDialog();
			
			break;
			
		case R.id.dialog_category_btn_close:
			finish();
			break;
		}
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.i("qq","position = "+position);
		String result = adapter.getItem(position);
		
		Intent intent = new Intent();
		
		intent.putExtra("type", getType(position));
		intent.putExtra("category", result);
		
		setResult(RESULT_OK, intent);
		finish();
		
	}
	
	private String getType(int position){
		String result="";
		if(position<3)
			result = String.valueOf(position);
		else
			result = "3";
		
		return result;
	}
	
	private void showInputDialog() {
		final Context context = CategoryDialog.this;
		AlertDialog.Builder builder;
		final AlertDialog dialog;
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dialog_input_category, null);
		
		final EditText edt = (EditText)layout.findViewById(R.id.dialog_input_edt_in);
		
		edt.setFilters(new InputFilter[]{HongUtil.filterAlpha});
		
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(edt, 0);
		Button okBtn = (Button)layout.findViewById(R.id.dialog_input_btn_ok);
		Button cancelBtn = (Button)layout.findViewById(R.id.dialog_input_btn_cancel);
		
		builder = new AlertDialog.Builder(context);
		builder.setView(layout);
		dialog = builder.create();
		dialog.setTitle("확장명을 '.'을 제외하고 입력해주세요(ex: gul)");
		
		
		okBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(edt.getText().toString().length()==0){
					HongUtil.makeToast(context, "입력 후 확인 버튼을 눌러주세요.");
				}
				else{
					addData(edt.getText().toString());
					categoryList = getCategory();
					adapter.setCategoryList(categoryList);
					adapter.notifyDataSetChanged();
				}
				dialog.dismiss();
			}

			
		});
		
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				dialog.dismiss();
				
			}
		});
		
		dialog.show();
	
	}
	private void addData(String str) {
		database.open();
		database.insertIndex(str);
		database.close();
	}

	private ArrayList<String> getCategory() {
		
		ArrayList<String> category = new ArrayList<String>();
		category.add("사진");
		category.add("동영상");
		category.add("음악");
		
		database.open();
		category.addAll(database.getIndex());
		database.close();
		
		return category;
	}

}
