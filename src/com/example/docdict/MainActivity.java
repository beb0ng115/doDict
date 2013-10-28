package com.example.docdict;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	EditText edtInput;
	Button btnShow;
	TextView tvMeans;
	List<words> wordsList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		edtInput = (EditText) findViewById(R.id.edtInput);
		btnShow = (Button) findViewById(R.id.btnShow);
		tvMeans = (TextView) findViewById(R.id.edtMean);

		dataPro data = new dataPro();
		wordsList = data.getListWord();

		btnShow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				//Cach lam : tu 1 tu input, ta lay ra vi tri va length cua no  (words)
				// Tu words vua tim ra, ta lay ra nghia cua no
				String inputWord = edtInput.getText().toString();
				words word = dataIndex.getWordsFromInput(inputWord, wordsList);
				if (word != null) {
					try {
						String meanning = dataMeaning.LoadMean(word);
						tvMeans.setText(meanning);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					Toast.makeText(getApplicationContext(), "Ban Nhap sai roi",
							1).show();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public class words {
		public String word;
		public String pos;
		public String length;
	}

	public class dataPro {

		// đọc CSDL từ file Inde lên
		// Sau đó cho vào 1 list Words
		
		public String readFile()
		{
			File wfile = new File(System.getProperty("user.dir").concat(
					Environment.getExternalStorageDirectory().getAbsolutePath()
							+ "/EnglishVietnamese.index"));
		   String content = null;
		   try {
			   FileInputStream fis = new FileInputStream(wfile);
		       byte[] data = new byte[(int)wfile.length()];
		       fis.read(data);
		       
		       fis.close();
		       content = new String(data, "UTF-8");
		   } catch (IOException e) {
		       e.printStackTrace();
		   }
		   return content;
		}
			
		public List<words> getListWord() {
			List<words> wordss = new ArrayList<words>();
			try {								
				String data = readFile();
				StringTokenizer token = new StringTokenizer(data, "\n");
				while (token.hasMoreTokens()) {
					String line = token.nextToken();
					String elements[] = line.split("\t");
					if (elements.length == 3) {
						words index = new words();
						index.word = elements[0];
						index.pos = elements[1];
						index.length = elements[2];
						wordss.add(index);
					}
				}
				return wordss;

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return wordss;
		}

	}

	public static class dataIndex {

		public static words getWordsFromInput(String input,
				List<words> listWords) {

			// từ 1 input, mình lấy ra được 1 words
			for (int i = 0; i < listWords.size(); i++) {
				if (input.equals(listWords.get(i).word)) {
					return listWords.get(i);
				}
			}

			return null;
		}
	}

	public static class dataMeaning {
		// Từ 1 words, ta lụm ra nghĩa của nó
		public static String LoadMean(words word) throws Exception {
			int offset = globalThing.convert64to10(word.pos);
			int lengths = globalThing.convert64to10(word.length);

			int n = 0;

			File mfile = new File(System.getProperty("user.dir").concat(
					Environment.getExternalStorageDirectory().getAbsolutePath()
							+ "/EnglishVietnamese.dict"));
			try {
				RandomAccessFile fileAccess = new RandomAccessFile(mfile, "r");
				fileAccess.seek(offset);
				byte[] dst = new byte[lengths];
				fileAccess.read(dst, 0, lengths);
				String meansning = new String(dst);// .replaceAll("\0+", "");
				return meansning;
			} catch (Exception exc) {
				n = 5;
			}
			return null;
			// Mỗi lần mình nhập, thì mình tìm 1 lần
			// - > thời gian tìm kiếm lâu, thời gian load nhanh
			// String mean =null
			// dau vo : offset -> length
			// dau ra : Nghia
			// 1. tu offset la leng, truy cap vao file dic voi offset va leng
			// FileAccessRandom
			// Lay nghia ra

			/*
			 * int size = address.size(); for (int i = 0; i < size; i++) { int
			 * address = this.address.get(i); int len = this.length.get(i);
			 * byte[] buff = new byte[len]; input.seek(address);
			 * input.read(buff, 0, len); String mean = new String(buff,
			 * "UTF8").replaceAll("\0+", ""); if (i == 0) { this.mean = mean; }
			 * else { if (!this.mean.contains(mean.trim())) { this.mean += '\n'
			 * + mean; } } } this.isLoaded = true;
			 */
		}
	}

	public static class globalThing {

		public static int convert64to10(String num) {
			int number = 0;
			int len = num.length();
			String code = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
			for (int i = 0; i < len; i++) {
				number += code.indexOf(num.charAt(i))
						* Math.pow(64, len - i - 1);
			}
			return number;
		}

		public static String convert10to64(int num) {
			String number = "";
			String code = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
			while (num != 0) {
				number = code.charAt(num % 64) + number;
				num = num / 64;
			}
			return number;
		}
	}

}
