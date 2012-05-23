/*
 * Remoteroid - A remote control solution for Android platform, including handy file transfer and notify-to-PC.
 * Copyright (C) 2012 Taeho Kim(jyte82@gmail.com), Hyomin Oh(ohmnia1112@gmail.com), Hongkyun Kim(godgjdgjd@nate.com), Yongwan Hwang(singerhwang@gmail.com)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package org.secmem.remoteroid.activity;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.fragment.AuthenticateFragment;
import org.secmem.remoteroid.fragment.DriverInstallationFragment;
import org.secmem.remoteroid.util.CommandLine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class Main extends SherlockFragmentActivity {
	
	private Fragment mAuthFragment;
	private Fragment mDriverFragment;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_red));
        mAuthFragment = new AuthenticateFragment();
        mDriverFragment = new DriverInstallationFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, mAuthFragment).commit();
    }
    

	@Override
	protected void onResume() {
		super.onResume();
		//Check driver existence
        if(!CommandLine.isDriverExists(getApplicationContext())){
                getSupportFragmentManager().beginTransaction().replace(R.id.container, mDriverFragment).commit();
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = new MenuInflater(this);
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.menu_main_preferences:
			startActivity(new Intent(this, NotificationReceiverSettings.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
}