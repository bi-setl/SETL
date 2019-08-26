package helper;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class AllFileOperations {
	String filename = "Resources.txt";

	public void writeToBinary(String filename, Object obj, boolean append) {
		File file = new File(filename);
		ObjectOutputStream out = null;

		try {
			if (!file.exists() || !append)
				out = new ObjectOutputStream(new FileOutputStream(filename));
			else
				out = new AppendingObjectOutputStream(new FileOutputStream(filename, append));
			out.writeObject(obj);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public ArrayList<Object> readFromBinaryFile(String filename) {
		File file = new File(filename);
		ArrayList<Object> list = new ArrayList<>();

		if (file.exists()) {
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(new FileInputStream(filename));
				while (true) {
					Object object = ois.readObject();
					list.add(object);
				}
			} catch (EOFException e) {
				// e.printStackTrace ();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (ois != null)
						ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	public void deleteFile(String filename) {
		// TODO Auto-generated method stub
		File file = new File(filename);

		try {
			file.delete();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
