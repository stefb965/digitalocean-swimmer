package com.yassirh.digitalocean.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yassirh.digitalocean.model.Account;

public class AccountDao extends SqlDao<Account> {

	private DatabaseHelper mDatabaseHelper;

	public AccountDao(DatabaseHelper databaseHelper) {
		super();
		this.mDatabaseHelper = databaseHelper;
	}

	public Account newInstance(Cursor c) {
		Account account = new Account();
		account.setId(c.getLong(c.getColumnIndex(AccountTable.ID)));
		account.setName(c.getString(c.getColumnIndex(AccountTable.NAME)));
		account.setToken(c.getString(c.getColumnIndex(AccountTable.TOKEN)));
		account.setSelected(c.getInt(c.getColumnIndex(AccountTable.SELECTED)) == 1);
		return account;
	}

	@Override
	public DatabaseHelper getDatabaseHelper() {
		return this.mDatabaseHelper;
	}

	@Override
	public TableHelper getTableHelper() {
		return new AccountTable();
	}

	public Long createOrUpdate(Account account) {
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		boolean update = account.getId() == null ? false : findById(account.getId()) != null;
		db = mDatabaseHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(AccountTable.ID, account.getId());
		values.put(AccountTable.NAME, account.getName());
		values.put(AccountTable.TOKEN, account.getToken());
		values.put(AccountTable.SELECTED, account.isSelected() ? 1 : 0);
		long id;
		if(update){
			id = account.getId();
			db.updateWithOnConflict(getTableHelper().TABLE_NAME,values,DropletTable.ID +"= ?",new String[]{id+""},SQLiteDatabase.CONFLICT_REPLACE);
		}else{
			id = db.insertWithOnConflict(getTableHelper().TABLE_NAME, null, values,SQLiteDatabase.CONFLICT_REPLACE);
		}
		return id;
	}

	public void unSelectAll() {
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(AccountTable.SELECTED, false);
		db.update(getTableHelper().TABLE_NAME, values, "1=1", null);
	}	
}
