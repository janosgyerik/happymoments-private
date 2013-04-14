package com.happymoments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class HappyMomentsFileManager {

	private static final String TAG = HappyMomentsFileManager.class.getSimpleName();

	public static final String BACKUPS_DIRPARAM = "HappyMoments/backups";
	public static final File BACKUPS_DIR =
			new File(Environment.getExternalStorageDirectory(), BACKUPS_DIRPARAM);

	public static final String PHOTOS_DIRPARAM = "HappyMoments/photos";
	public static final File PHOTOS_DIR =
			new File(Environment.getExternalStorageDirectory(), PHOTOS_DIRPARAM);

	private static final String MEDIUM_PHOTOS_DIRPARAM = "HappyMoments/medium";
	private static final File MEDIUM_PHOTOS_DIR =
			new File(Environment.getExternalStorageDirectory(), MEDIUM_PHOTOS_DIRPARAM);
	private static final int MEDIUM_PHOTO_FACTOR = 1;

	private static final String SMALL_PHOTOS_DIRPARAM = "HappyMoments/small";
	private static final File SMALL_PHOTOS_DIR =
			new File(Environment.getExternalStorageDirectory(), SMALL_PHOTOS_DIRPARAM);
	private static final int SMALL_PHOTO_FACTOR = 2;

	public static final String BACKUPFILE_FORMAT = "";
	public static final String BACKUPFILES_PATTERN = "^sqlite3-.*\\.db$";
	public static final String DAILY_BACKUPFILE = "sqlite3-autobackup.db";

	public static final String HAPPYMOMENT_PHOTOFILE_FORMAT = "happymoment_%s_%d.jpg";
	public static final String HAPPYMOMENT_PHOTOFILES_PATTERN = "^happymoment_%s_.*";


	private static String getDatabasePath(String packageName) {
		String dbname = "sqlite3.db";
		return String.format("/data/%s/databases/%s", packageName, dbname);
	}

	public static File getBackupFile(String filename) {
		return new File(BACKUPS_DIR, filename);
	}

	public static boolean backupDatabaseFile(String packageName) throws IOException {
		String filename = String.format("sqlite3-%s.db",
				new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(new Date()));
		return backupDatabaseFile(filename, packageName);
	}

	private static boolean backupDatabaseFile(String filename, String packageName) throws IOException {
		if (isExternalStorageWritable()) {
			File backupDir = BACKUPS_DIR;
			if (! backupDir.isDirectory()) {
				backupDir.mkdirs();
			}
			
			File dataDir = Environment.getDataDirectory();
			File currentDB = new File(dataDir, getDatabasePath(packageName));
			File backupFile = new File(backupDir, filename);

			FileChannel src = new FileInputStream(currentDB).getChannel();
			FileChannel dst = new FileOutputStream(backupFile).getChannel();
			dst.transferFrom(src, 0, src.size());
			src.close();
			dst.close();
			return true;
		}
		return false;
	}

	public static boolean restoreDatabaseFile(String filename, String packageName) throws IOException {
		File dataDir = Environment.getDataDirectory();

		File currentFile = new File(dataDir, getDatabasePath(packageName));
		File backupFile = new File(BACKUPS_DIR, filename);

		FileChannel src = new FileInputStream(backupFile).getChannel();
		FileChannel dst = new FileOutputStream(currentFile).getChannel();
		dst.transferFrom(src, 0, src.size());
		src.close();
		dst.close();
		return true;
	}

	public static File getPhotoFile(String filename) {
		return new File(PHOTOS_DIR, filename);
	}

	public static File getMediumPhotoFile(String filename) {
		if (! MEDIUM_PHOTOS_DIR.isDirectory()) {
			MEDIUM_PHOTOS_DIR.mkdirs();
		}
		return new File(MEDIUM_PHOTOS_DIR, filename);
	}

	public static File getSmallPhotoFile(String filename) {
		if (! SMALL_PHOTOS_DIR.isDirectory()) {
			SMALL_PHOTOS_DIR.mkdirs();
		}
		return new File(SMALL_PHOTOS_DIR, filename);
	}

	public static File newPhotoFile(String happyMomentId) throws IOException {
		if (! PHOTOS_DIR.isDirectory()) {
			PHOTOS_DIR.mkdirs();
		}
		return File.createTempFile(String.format("happymoment_%s_", happyMomentId),
				".jpg", PHOTOS_DIR);
	}
	
	public static File newPhotoFile() throws IOException {
		return newPhotoFile("tmp");
	}

	/**
	 * Update daily database backup, only once a day.
	 */
	public static void updateDailyBackup(String packageName) {
		Date now = new Date();
		Date lastModified = null;
		File backupFile = getBackupFile(DAILY_BACKUPFILE);
		if (backupFile.isFile()) {
			lastModified = new Date(backupFile.lastModified());
		}
		if (lastModified == null || lastModified.getDay() < now.getDay()) {
			try {
				backupDatabaseFile(DAILY_BACKUPFILE, packageName);
				Log.d(TAG, "Updated daily backup");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean isExternalStorageWritable() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	private static Bitmap createScaledPhotoBitmap(File srcFile, File dstFile, int dstWidth, int inSampleSize) {
		if (srcFile.isFile()) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = inSampleSize;
			Bitmap bitmap = BitmapFactory.decodeFile(srcFile.getAbsolutePath(), options);
			int dstHeight = dstWidth * bitmap.getHeight() / bitmap.getWidth();
			Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, true);

			if (!scaledBitmap.equals(bitmap)) {
				bitmap.recycle();
				bitmap = null;
			}

			FileOutputStream outStream;
			try {
				outStream = new FileOutputStream(dstFile);
				scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
				Log.i(TAG, String.format("resized photo: %dx%d %s", dstWidth, dstHeight, dstFile));
				return scaledBitmap;
			} catch (FileNotFoundException e) {
				Log.e(TAG, "could not save resized photo: " + dstFile);
				e.printStackTrace();
			}
		}
		return null;
	}

	public static Bitmap getPhotoBitmap(String photoFilename) {
		File srcFile = getPhotoFile(photoFilename);
		return BitmapFactory.decodeFile(srcFile.getAbsolutePath());
	}

	public static Bitmap getMediumPhotoBitmap(String photoFilename, int appWidth) {
		File dstFile = getMediumPhotoFile(photoFilename);
		if (dstFile.isFile()) {
			return BitmapFactory.decodeFile(dstFile.getAbsolutePath());
		}
		File srcFile = getPhotoFile(photoFilename);
		int dstWidth = appWidth / MEDIUM_PHOTO_FACTOR;
		int inSampleSize = MEDIUM_PHOTO_FACTOR;
		return createScaledPhotoBitmap(srcFile, dstFile, dstWidth, inSampleSize);
	}

	public static Bitmap getSmallPhotoBitmap(String photoFilename, int appWidth) {
		File dstFile = getSmallPhotoFile(photoFilename);
		if (dstFile.isFile()) {
			return BitmapFactory.decodeFile(dstFile.getAbsolutePath());
		}
		File srcFile = getPhotoFile(photoFilename);
		int dstWidth = appWidth / SMALL_PHOTO_FACTOR;
		int inSampleSize = SMALL_PHOTO_FACTOR;
		return createScaledPhotoBitmap(srcFile, dstFile, dstWidth, inSampleSize);
	}
}
