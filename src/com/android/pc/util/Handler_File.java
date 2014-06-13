package com.android.pc.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.android.pc.ioc.app.Ioc;

/**
 * File Utils
 * <ul>
 * Read or write file
 * <li>{@link #readFile(String)} read file</li>
 * <li>{@link #readFileToList(String)} read file to string list</li>
 * <li>{@link #writeFile(String, String, boolean)} write file</li>
 * <li>{@link #writeFile(String, InputStream)} write file</li>
 * </ul>
 * <ul>
 * Operate file
 * <li>{@link #getFileExtension(String)}</li>
 * <li>{@link #getFileName(String)}</li>
 * <li>{@link #getFileNameWithoutExtension(String)}</li>
 * <li>{@link #getFileSize(String)}</li>
 * <li>{@link #deleteFile(String)}</li>
 * <li>{@link #isFileExist(String)}</li>
 * <li>{@link #isFolderExist(String)}</li>
 * <li>{@link #makeFolders(String)}</li>
 * <li>{@link #makeDirs(String)}</li>
 * </ul>
 * 
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2012-5-12
 */
public class Handler_File {

	public final static String FILE_EXTENSION_SEPARATOR = ".";

	/**
	 * read file
	 * 
	 * @param filePath
	 * @return if file not exist, return null, else return content of file
	 * @throws IOException
	 *             if an error occurs while operator BufferedReader
	 */
	public static StringBuilder readFile(String filePath) {
		File file = new File(filePath);
		StringBuilder fileContent = new StringBuilder("");
		if (file == null || !file.isFile()) {
			return null;
		}

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!fileContent.toString().equals("")) {
					fileContent.append("\r\n");
				}
				fileContent.append(line);
			}
			reader.close();
			return fileContent;
		} catch (IOException e) {
			throw new RuntimeException("IOException occurred. ", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new RuntimeException("IOException occurred. ", e);
				}
			}
		}
	}

	/**
	 * write file
	 * 
	 * @param filePath
	 * @param content
	 * @param append
	 *            is append, if true, write to the end of file, else clear content of file and write into it
	 * @return return true
	 * @throws IOException
	 *             if an error occurs while operator FileWriter
	 */
	public static boolean writeFile(String filePath, String content, boolean append) {
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(filePath, append);
			fileWriter.write(content);
			fileWriter.close();
			return true;
		} catch (IOException e) {
			throw new RuntimeException("IOException occurred. ", e);
		} finally {
			if (fileWriter != null) {
				try {
					fileWriter.close();
				} catch (IOException e) {
					throw new RuntimeException("IOException occurred. ", e);
				}
			}
		}
	}

	/**
	 * write file
	 * 
	 * @param filePath
	 * @param stream
	 * @return return true
	 * @throws IOException
	 *             if an error occurs while operator FileWriter
	 */
	public static boolean writeFile(String filePath, InputStream stream) {
		OutputStream o = null;
		try {
			o = new FileOutputStream(filePath);
			byte data[] = new byte[1024];
			int length = -1;
			while ((length = stream.read(data)) != -1) {
				o.write(data, 0, length);
			}
			o.flush();
			return true;
		} catch (FileNotFoundException e) {
			throw new RuntimeException("FileNotFoundException occurred. ", e);
		} catch (IOException e) {
			throw new RuntimeException("IOException occurred. ", e);
		} finally {
			if (o != null) {
				try {
					o.close();
					stream.close();
				} catch (IOException e) {
					throw new RuntimeException("IOException occurred. ", e);
				}
			}
		}
	}

	/**
	 * read file to string list, a element of list is a line
	 * 
	 * @param filePath
	 * @return if file not exist, return null, else return content of file
	 * @throws IOException
	 *             if an error occurs while operator BufferedReader
	 */
	public static List<String> readFileToList(String filePath) {
		File file = new File(filePath);
		List<String> fileContent = new ArrayList<String>();
		if (file == null || !file.isFile()) {
			return null;
		}

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				fileContent.add(line);
			}
			reader.close();
			return fileContent;
		} catch (IOException e) {
			throw new RuntimeException("IOException occurred. ", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new RuntimeException("IOException occurred. ", e);
				}
			}
		}
	}

	/**
	 * get file name from path, not include suffix
	 * 
	 * <pre>
	 *      getFileNameWithoutExtension(null)               =   null
	 *      getFileNameWithoutExtension("")                 =   ""
	 *      getFileNameWithoutExtension("   ")              =   "   "
	 *      getFileNameWithoutExtension("abc")              =   "abc"
	 *      getFileNameWithoutExtension("a.mp3")            =   "a"
	 *      getFileNameWithoutExtension("a.b.rmvb")         =   "a.b"
	 *      getFileNameWithoutExtension("c:\\")              =   ""
	 *      getFileNameWithoutExtension("c:\\a")             =   "a"
	 *      getFileNameWithoutExtension("c:\\a.b")           =   "a"
	 *      getFileNameWithoutExtension("c:a.txt\\a")        =   "a"
	 *      getFileNameWithoutExtension("/home/admin")      =   "admin"
	 *      getFileNameWithoutExtension("/home/admin/a.txt/b.mp3")  =   "b"
	 * </pre>
	 * 
	 * @param filePath
	 * @return file name from path, not include suffix
	 * @see
	 */
	public static String getFileNameWithoutExtension(String filePath) {
		if (Handler_String.isEmpty(filePath)) {
			return filePath;
		}

		int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
		int filePosi = filePath.lastIndexOf(File.separator);
		if (filePosi == -1) {
			return (extenPosi == -1 ? filePath : filePath.substring(0, extenPosi));
		}
		if (extenPosi == -1) {
			return filePath.substring(filePosi + 1);
		}
		return (filePosi < extenPosi ? filePath.substring(filePosi + 1, extenPosi) : filePath.substring(filePosi + 1));
	}

	/**
	 * get file name from path, include suffix
	 * 
	 * <pre>
	 *      getFileName(null)               =   null
	 *      getFileName("")                 =   ""
	 *      getFileName("   ")              =   "   "
	 *      getFileName("a.mp3")            =   "a.mp3"
	 *      getFileName("a.b.rmvb")         =   "a.b.rmvb"
	 *      getFileName("abc")              =   "abc"
	 *      getFileName("c:\\")              =   ""
	 *      getFileName("c:\\a")             =   "a"
	 *      getFileName("c:\\a.b")           =   "a.b"
	 *      getFileName("c:a.txt\\a")        =   "a"
	 *      getFileName("/home/admin")      =   "admin"
	 *      getFileName("/home/admin/a.txt/b.mp3")  =   "b.mp3"
	 * </pre>
	 * 
	 * @param filePath
	 * @return file name from path, include suffix
	 */
	public static String getFileName(String filePath) {
		if (Handler_String.isEmpty(filePath)) {
			return filePath;
		}

		int filePosi = filePath.lastIndexOf(File.separator);
		return (filePosi == -1) ? filePath : filePath.substring(filePosi + 1);
	}

	/**
	 * get folder name from path
	 * 
	 * <pre>
	 *      getFolderName(null)               =   null
	 *      getFolderName("")                 =   ""
	 *      getFolderName("   ")              =   ""
	 *      getFolderName("a.mp3")            =   ""
	 *      getFolderName("a.b.rmvb")         =   ""
	 *      getFolderName("abc")              =   ""
	 *      getFolderName("c:\\")              =   "c:"
	 *      getFolderName("c:\\a")             =   "c:"
	 *      getFolderName("c:\\a.b")           =   "c:"
	 *      getFolderName("c:a.txt\\a")        =   "c:a.txt"
	 *      getFolderName("c:a\\b\\c\\d.txt")    =   "c:a\\b\\c"
	 *      getFolderName("/home/admin")      =   "/home"
	 *      getFolderName("/home/admin/a.txt/b.mp3")  =   "/home/admin/a.txt"
	 * </pre>
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFolderName(String filePath) {

		if (Handler_String.isEmpty(filePath)) {
			return filePath;
		}

		int filePosi = filePath.lastIndexOf(File.separator);
		return (filePosi == -1) ? "" : filePath.substring(0, filePosi);
	}

	/**
	 * get suffix of file from path
	 * 
	 * <pre>
	 *      getFileExtension(null)               =   ""
	 *      getFileExtension("")                 =   ""
	 *      getFileExtension("   ")              =   "   "
	 *      getFileExtension("a.mp3")            =   "mp3"
	 *      getFileExtension("a.b.rmvb")         =   "rmvb"
	 *      getFileExtension("abc")              =   ""
	 *      getFileExtension("c:\\")              =   ""
	 *      getFileExtension("c:\\a")             =   ""
	 *      getFileExtension("c:\\a.b")           =   "b"
	 *      getFileExtension("c:a.txt\\a")        =   ""
	 *      getFileExtension("/home/admin")      =   ""
	 *      getFileExtension("/home/admin/a.txt/b")  =   ""
	 *      getFileExtension("/home/admin/a.txt/b.mp3")  =   "mp3"
	 * </pre>
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileExtension(String filePath) {
		if (Handler_String.isBlank(filePath)) {
			return filePath;
		}

		int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
		int filePosi = filePath.lastIndexOf(File.separator);
		if (extenPosi == -1) {
			return "";
		}
		return (filePosi >= extenPosi) ? "" : filePath.substring(extenPosi + 1);
	}

	/**
	 * Creates the directory named by the trailing filename of this file, including the complete directory path required to create this directory. <br/>
	 * <br/>
	 * <ul>
	 * <strong>Attentions:</strong>
	 * <li>makeDirs("C:\\Users\\Trinea") can only create users folder</li>
	 * <li>makeFolder("C:\\Users\\Trinea\\") can create Trinea folder</li>
	 * </ul>
	 * 
	 * @param filePath
	 * @return true if the necessary directories have been created or the target directory already exists, false one of the directories can not be created.
	 *         <ul>
	 *         <li>if {@link Handler_File#getFolderName(String)} return null, return false</li>
	 *         <li>if target directory already exists, return true</li>
	 *         <li>return {@link java.io.File#makeFolder}</li>
	 *         </ul>
	 */
	public static boolean makeDirs(String filePath) {
		String folderName = getFolderName(filePath);
		if (Handler_String.isEmpty(folderName)) {
			return false;
		}

		File folder = new File(folderName);
		return (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
	}

	/**
	 * @param filePath
	 * @return
	 * @see #makeDirs(String)
	 */
	public static boolean makeFolders(String filePath) {
		return makeDirs(filePath);
	}

	/**
	 * Indicates if this file represents a file on the underlying file system.
	 * 
	 * @param filePath
	 * @return
	 */
	public static boolean isFileExist(String filePath) {
		if (Handler_String.isBlank(filePath)) {
			return false;
		}

		File file = new File(filePath);
		return (file.exists() && file.isFile());
	}

	/**
	 * Indicates if this file represents a directory on the underlying file system.
	 * 
	 * @param directoryPath
	 * @return
	 */
	public static boolean isFolderExist(String directoryPath) {
		if (Handler_String.isBlank(directoryPath)) {
			return false;
		}

		File dire = new File(directoryPath);
		return (dire.exists() && dire.isDirectory());
	}

	/**
	 * delete file or directory
	 * <ul>
	 * <li>if path is null or empty, return true</li>
	 * <li>if path not exist, return true</li>
	 * <li>if path exist, delete recursion. return true</li>
	 * <ul>
	 * 
	 * @param path
	 * @return
	 */
	public static boolean deleteFile(String path) {
		if (Handler_String.isBlank(path)) {
			return true;
		}

		File file = new File(path);
		if (!file.exists()) {
			return true;
		}
		if (file.isFile()) {
			return file.delete();
		}
		if (!file.isDirectory()) {
			return false;
		}
		for (File f : file.listFiles()) {
			if (f.isFile()) {
				f.delete();
			} else if (f.isDirectory()) {
				deleteFile(f.getAbsolutePath());
			}
		}
		return file.delete();
	}

	/**
	 * 获取文件大小
	 * <ul>
	 * <li>if path is null or empty, return -1</li>
	 * <li>if path exist and it is a file, return file size, else return -1</li>
	 * <ul>
	 * 
	 * @param path
	 * @return
	 */
	public static long getFileSize(String path) {
		if (Handler_String.isBlank(path)) {
			return -1;
		}

		File file = new File(path);
		return (file.exists() && file.isFile() ? file.length() : -1);
	}

	/**
	 * 文件拷贝
	 * 
	 * @author gdpancheng@gmail.com 2014-2-15 下午7:15:51
	 * @param sourceFile
	 * @param targetFile
	 * @throws IOException
	 * @return void
	 */
	public static void copyFile(File sourceFile, File targetFile) {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			// 新建文件输入流并对它进行缓冲
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
			// 新建文件输出流并对它进行缓冲
			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
			// 缓冲数组
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			// 刷新此缓冲的输出流
			outBuff.flush();
			sourceFile.delete();
			sourceFile.deleteOnExit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭流
			try {
				if (inBuff != null)
					inBuff.close();
				if (outBuff != null)
					outBuff.close();
			} catch (IOException e) {
			}
		}
	}

	public static File getExternalCacheDir(Context context, String dirs_name) {
		File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
		File appCacheDir = new File(new File(new File(dataDir, context.getPackageName()), "cache"), dirs_name);
		if (!appCacheDir.exists()) {
			try {
				new File(dataDir, ".nomedia").createNewFile();
			} catch (IOException e) {
				Log.w("创建目录", "Can't create \".nomedia\" file in application external cache directory", e);
			}
			if (!appCacheDir.mkdirs()) {
				Log.w("创建目录", "Unable to create external cache directory");
				return null;
			}
		}
		return appCacheDir;
	}

	public static void write(File file, String data) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(file), 1024);
			out.write(data);
		} catch (IOException e) {
			Ioc.getIoc().getLogger().d("write " + file.getAbsolutePath() + " data failed!");
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String getAsString(File file) {
		if (!file.exists())
			return null;
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(file));
			String readString = "";
			String currentLine;
			while ((currentLine = in.readLine()) != null) {
				readString += currentLine;
			}
			return readString;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void saveObject(String fileName,Serializable object) {
		FileOutputStream fos = null;
		 ObjectOutputStream oos = null;
		try {
			fos = Ioc.getIoc().getApplication().openFileOutput(fileName,Context.MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
	        oos.writeObject(object);// 写入
        } catch (Exception e) {
	        e.printStackTrace();
        } finally{
        	try {
	            if (fos!=null) {
	            	fos.close();
	            }
	            if (oos!=null) {
	            	oos.close();
	            }
            } catch (IOException e) {
            }
        }
    }
	
	public static <T>T getObject(String fileName) {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis=Ioc.getIoc().getApplication().openFileInput(fileName);   //获得输入流
			ois = new ObjectInputStream(fis);
			return (T) ois.readObject();
        } catch (Exception e) {
	        e.printStackTrace();
        }finally{
        	try {
	            if (fis!=null) {
	            	fis.close();
	            }
	            if (ois!=null) {
	            	ois.close();
	            }
            } catch (IOException e) {
            }
        }
		return null;
    }
}
