package com.contacts.dao;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;

import com.contacts.model.OAuthDetails;
import com.contacts.querylayer.Column;
import com.contacts.querylayer.QueryBuilder;
import com.contacts.querylayer.QueryExecutor;
import com.contacts.utils.Database.*;
import com.contacts.utils.Operators;

public class OAuthDAO {
	public static int addSyncMail(OAuthDetails details) throws ClassNotFoundException, SQLException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.insertTable(TableInfo.OAUTHDETAILS);
		qb.insertValuesToColumns(new Column(OAuthDetailsEnum.USERID, "", "", qb.table), details.getUserId());
		qb.insertValuesToColumns(new Column(OAuthDetailsEnum.EMAIL, "", "", qb.table), details.getEmail());
		qb.insertValuesToColumns(new Column(OAuthDetailsEnum.REFRESHTOKEN, "", "", qb.table),
				details.getRefreshToken());
		qb.insertValuesToColumns(new Column(OAuthDetailsEnum.ACCESSTOKEN, "", "", qb.table), details.getAccessToken());
		qb.insertValuesToColumns(new Column(OAuthDetailsEnum.CREATEDAT, "", "", qb.table), details.getCreatedAt());
		qb.insertValuesToColumns(new Column(OAuthDetailsEnum.MODIFIEDAT, "", "", qb.table), details.getModifiedAt());
		return qx.executeAndUpdateWithKeys(qb.build());
	}

	public static int updateSyncMail(OAuthDetails details) throws ClassNotFoundException, SQLException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.updateTable(TableInfo.OAUTHDETAILS);
		qb.updateColumn(new Column(OAuthDetailsEnum.REFRESHTOKEN, "", "", qb.table), details.getRefreshToken());
		qb.updateColumn(new Column(OAuthDetailsEnum.ACCESSTOKEN, "", "", qb.table), details.getAccessToken());
		qb.updateColumn(new Column(OAuthDetailsEnum.MODIFIEDAT, "", "", qb.table), details.getModifiedAt());
		qb.setCondition(new Column(OAuthDetailsEnum.USERID, "", "", qb.table), Operators.EQUAL, details.getUserId());
		qb.setCondition(new Column(OAuthDetailsEnum.EMAIL, "", "", qb.table), Operators.EQUAL, details.getEmail());
		return qx.executeAndUpdateWithKeys(qb.build());
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<OAuthDetails> getAllAvailableSyncMails(int userId)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.selectTable(TableInfo.OAUTHDETAILS);
		qb.setCondition(new Column(OAuthDetailsEnum.USERID, "", "", qb.table), Operators.EQUAL, userId);
		return (ArrayList<OAuthDetails>) qx.executeQuery(qb.build());
	}

	@SuppressWarnings("unchecked")
	public static OAuthDetails getSyncMailById(int oauthId) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.selectTable(TableInfo.OAUTHDETAILS);
		qb.setCondition(new Column(OAuthDetailsEnum.ID, "", "", qb.table), Operators.EQUAL, oauthId);
		ArrayList<OAuthDetails> syncedMails = (ArrayList<OAuthDetails>) qx.executeQuery(qb.build());
		if (syncedMails.size() > 0)
			return syncedMails.get(0);
		return null;
	}

	@SuppressWarnings("unchecked")
	public static boolean isSyncMailExist(OAuthDetails oauth) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		QueryBuilder qb = new QueryBuilder();
		QueryExecutor qx = new QueryExecutor();
		qb.selectTable(TableInfo.OAUTHDETAILS);
		qb.setCondition(new Column(OAuthDetailsEnum.USERID, "", "", qb.table), Operators.EQUAL, oauth.getUserId());
		qb.setCondition(new Column(OAuthDetailsEnum.EMAIL, "", "", qb.table), Operators.EQUAL, oauth.getEmail());
		ArrayList<OAuthDetails> syncedMails = (ArrayList<OAuthDetails>) qx.executeQuery(qb.build());
		return syncedMails.size() > 0;
	}
}
