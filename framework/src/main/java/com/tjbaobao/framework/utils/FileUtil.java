package com.tjbaobao.framework.utils;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import com.tjbaobao.framework.listener.OnProgressListener;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * 文件工具类
 * @author Tjbaobao
 * @time 2016年11月25日 下午3:27:29
 */
@SuppressWarnings({"JavaDoc", "UnusedReturnValue", "unused", "WeakerAccess", "SameParameterValue"})
public class FileUtil {
	private static final String DEF_CHARSET_NAME = "iso8859-1";
	private static final int DEF_SIZE_BUFF =1024*4;//默认缓存区字节大小
	//--------------------------Write
	@SuppressWarnings({"JavaDoc", "WeakerAccess", "UnusedReturnValue", "unused"})
	public static class Writer{
		/**
		 * 将制定字节在指定文件的指定位置写入
		 * @param bytes
		 * @param position
		 * @param path
		 * @return
		 */
		public static boolean writeFile(byte[] bytes, long position, String path) {
			createFolder(path);
			boolean bOk = false;
			RandomAccessFile randomAccessFile = null;
			try {
				randomAccessFile = new RandomAccessFile(path, "rw");
				randomAccessFile.seek(position);
				randomAccessFile.write(bytes);
				bOk = true;
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (randomAccessFile != null)
						randomAccessFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return bOk;
		}

		public static boolean writeFile(byte[] bytes, String path) {
			createFolder(path);
			boolean bOk = false;
			File file = new File(path);
			OutputStream out = null;
			try {
				out = new FileOutputStream(file);
				out.write(bytes);
                bOk = true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (out != null)
						out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return bOk;
		}

		/**
		 * 将指定文本以指定的编码写到指定文件
		 * 
		 * @param str
		 * @param path
		 * @param charsetName
		 * @return
		 */
		public static boolean writeFile(String str, String path, String charsetName) {
			try {
				return writeFile(str.getBytes(charsetName), path);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return false;
		}

		public static boolean writeFile(String str, String path) {
			return writeFile(str, path, DEF_CHARSET_NAME);
		}

		/**
		 * 将字节数组追加到文件末尾
		 * @param bytes
		 * @param path
		 * @return
		 */
		public static boolean writeFileAtEnd(byte[] bytes, String path) {
			createFolder(path);
			File file = new File(path);
			long length = file.length();
			return writeFile(bytes, length, path);
		}

		/**
		 * 将字符串追加到文件末尾
		 * @param str
		 * @param path
		 * @param charsetName 编码
		 * @return
		 */
		public static boolean writeFileAtEnd(String str, String path, String charsetName) {
			byte[] bytes;
			try {
				bytes = str.getBytes(charsetName);
				return writeFileAtEnd(bytes, path);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}

		/**
		 * 将字符串追加到文件末尾
		 * 
		 * @param str
		 * @param path
		 * @return
		 */
		public static boolean writeFileAtEnd(String str, String path) {
			return writeFileAtEnd(str, path, DEF_CHARSET_NAME);
		}

		/**
		 * 通过输入流写入文件
		 * @param inputStream
		 * @param path
		 * @return
		 */
		public static boolean writeFile(InputStream inputStream,String path)
		{
			return writeFile(inputStream,path,null);
		}

		public static boolean writeFile(InputStream inputStream,String path,OnProgressListener onProgressListener)
		{
			if(inputStream==null||path==null)
			{
				return false;
			}
			createFolder(path);
			byte[] byteBuff = new byte[DEF_SIZE_BUFF];
			try {
				FileUtil.delFileIfExists(path);
				FileOutputStream fileOutputStream = new FileOutputStream(new File(path));
				long readSize = 0;
				int len ;
				while ((len=inputStream.read(byteBuff))>0)
				{
					fileOutputStream.write(byteBuff,0,len);
					readSize+=len;
					if(onProgressListener!=null)
					{
						onProgressListener.onProgress(readSize);
					}
				}
				fileOutputStream.flush();
				fileOutputStream.close();
				inputStream.close();
				return true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}
	}
	
	//--------------------------Read
	@SuppressWarnings({"JavaDoc", "WeakerAccess", "unused"})
	public static class Reader{

		@Nullable
		public static byte[] readToBytes(@NonNull InputStream inStream){
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			try{
				byte[] buffer = new byte[DEF_SIZE_BUFF];
				int len ;
				while ((len=inStream.read(buffer))!=-1){
					outStream.write(buffer,0,len);
				}
				return outStream.toByteArray();
			}catch (IOException e){
				e.printStackTrace();
				return null;
			}finally {
				CloseUtil.closeIO(outStream);
			}
		}

		@Nullable
		public static byte[] readToBytes(@NonNull File file){
			FileInputStream inStream = null;
			FileChannel inChannel;
			try{
				inStream = new FileInputStream(file);
				inChannel = inStream.getChannel();
				ByteBuffer buffer = ByteBuffer.allocate((int) inChannel.size());
				inChannel.read(buffer);
				return buffer.array();
			}catch (IOException e){
				e.printStackTrace();
				return null;
			}finally {
				CloseUtil.closeIO(inStream);
			}
		}

		@Nullable
		public static byte[] readToBytes(@NonNull String path){
			return readToBytes(new File(path));
		}

		@Nullable
		public static String readToText(@NonNull InputStream inputStream,String charsetName){
			byte[] bytes = readToBytes(inputStream);
			if(bytes!=null){
				try {
					return new String(bytes,charsetName);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					return null;
				}
			}
			return null;
		}

		@Nullable
		public static String readToText(@NonNull File file,String charsetName){
			byte[] bytes = readToBytes(file);
			if(bytes!=null){
				try {
					return new String(bytes,charsetName);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					return null;
				}
			}
			return null;
		}

		@Nullable
		public static String readToText(@NonNull String path,String charsetName){
			return readToText(new File(path),charsetName);
		}

		@Nullable
		public static String readToText(@NonNull InputStream inputStream){
			byte[] bytes = readToBytes(inputStream);
			if(bytes!=null){
				return new String(bytes);
			}
			return null;
		}

		@Nullable
		public static String readToText(@NonNull File file){
			byte[] bytes = readToBytes(file);
			if(bytes!=null){
				return new String(bytes);
			}
			return null;
		}

		@Nullable
		public static String readToText(@NonNull String path){
			return readToText(new File(path));
		}

		/**
		 * 在指定文件的指定位置读取指定大小的内容到字节数组
		 * @param position 开始读取的位置
		 * @param size 读取的大小，不宜过大，需要考虑内存
		 * @param path path
		 * @return byte[]
		 */
		public static byte[] readFileToBytes(long position, int size, String path) {
			RandomAccessFile randomAccessFile = null;
			byte[] byteBuffer = null;
			try {
				randomAccessFile = new RandomAccessFile(path, "rw");
				long fileLength = randomAccessFile.length();
				if (position + size >= fileLength) {
					size = (int) (fileLength - position);
				}
				if (size > 0) {
					randomAccessFile.seek(position);// 将文件流的位置移动到pos字节处
					byteBuffer = new byte[size];
					randomAccessFile.read(byteBuffer, 0, size);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (randomAccessFile != null)
						randomAccessFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return byteBuffer;
		}

		/**
		 * @deprecated 建议使用@{{@link #readToBytes(String)}}
		 * @param path
		 * @return
		 */
		@Deprecated
		public static byte[] readFileToBytes(@NonNull String path)
		{
			File file = new File(path);
			if(file.exists())
			{
				return readFileToBytes(0, (int) file.length(),path);
			}
			return null;
		}

		/**
		 * @deprecated 建议使用@{@link #readToText(InputStream)}
		 * @param inputStream
		 * @return String
		 */
		@Deprecated
		public static String readTextByInputSteam(InputStream inputStream)
		{
			if(inputStream==null) return null;
			String str ;
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			StringBuilder stringBuffer = new StringBuilder();
			try {
				while ((str=reader.readLine())!=null)
                {
					stringBuffer.append(str);
                }
                inputStream.close();
				reader.close();
                return stringBuffer.toString();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * @deprecated @{@link #readToText(String)}
		 * @param path
		 * @return
		 */
		@Deprecated
		public static String readTextByPath(String path)
		{
			File file = new File(path);
			if(file.exists())
			{
				try {
					StringBuilder stringBuffer = new StringBuilder();
					FileInputStream fileInputStream = new FileInputStream(file);
					InputStreamReader reader = new InputStreamReader(fileInputStream);
					BufferedReader bufferedReader = new BufferedReader(reader);
					String lineText ;
					while((lineText=bufferedReader.readLine())!=null)
					{
						stringBuffer.append(lineText);
					}
					bufferedReader.close();
					reader.close();
					fileInputStream.close();
					return stringBuffer.toString();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		/**
		 * 一行一行的将文本读取出来
		 * @param path inPath
		 * @return lineStringList
		 */
		public static ArrayList<String> readTextLineListByPath(String path)
		{
			File file = new File(path);
			if(file.exists())
			{
				try {
					ArrayList<String> stringList = new ArrayList<>();
					FileInputStream fileInputStream = new FileInputStream(file);
					InputStreamReader reader = new InputStreamReader(fileInputStream);
					BufferedReader bufferedReader = new BufferedReader(reader);
					String lineText ;
					while((lineText=bufferedReader.readLine())!=null)
					{
						stringList.add(lineText);
					}
					bufferedReader.close();
					reader.close();
					fileInputStream.close();
					return stringList;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

	}
	
	//--------------------------Find
	/**
	 * 从指定文件中寻找指定字符串，并返回该字符串在文件中的位置
	 * @param path path
	 * @return 位置,如果发生错误或为找到该字符串，则返回-1
	 */
	public static ArrayList<Long> indexOf(long position,long length,String str,String path)
	{
		int size = 1024 *100;
		long index ;
		int offset ;//偏移量
		long readSize =0;
		ArrayList<Long> list = new ArrayList<>();
		try {
			offset = str.getBytes(DEF_CHARSET_NAME).length;
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return list;
		}
		if(length<size)
		{
			size = (int) length ;
		}
		
		int i=0;
		while(readSize<length)
		{
			byte[] bytesMP4= Reader.readFileToBytes(position,size,path);
			if(bytesMP4==null||bytesMP4.length<=0)
			{
				return list ;
			}
			int indexFindTemp;
			try {
				indexFindTemp = new String(bytesMP4,DEF_CHARSET_NAME).indexOf(str);
				if(indexFindTemp>0)
				{
					index = (int) (position+indexFindTemp);
					list.add(index);
					readSize+=index+offset;
					position+=index+offset;
					
				}
				else
				{
					readSize+=size-offset;
					position+=size-offset ;
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			i++;
		}
		return list;
	}

	//--------------------------Delete
	/** 删除文件，如果文件存在
	 * @param path
	 * @return
	 */
    public static boolean delFileIfExists(String path) {
        if (path == null) {
            return false;
        }
        File file = new File(path);
        return file.exists() && file.delete();
    }

	//--------------------------Tools
	/**
	 * 任务耗时统计类
	 * @author Tjbaobao
	 * @time 2016年11月23日 下午1:54:13
	 */
	public static class CurrentTime
	{
		@SuppressLint("UseSparseArrays")
        private static Map<Integer, Long> mapBeginTime = new HashMap<>();

		@SuppressLint("UseSparseArrays")
		private static Map<Integer, Long> mapPauseTime = new HashMap<>();
		private static int defTag = 0 ;
		public static void begin()
		{
			begin(defTag);
		}
		public static void begin(int tag)
		{
			mapBeginTime.put(tag, System.currentTimeMillis());
		}

		public static long pause(String tip)
		{
			return pause(tip,0);
		}

		public static long pause(String tip,int tag)
		{
			long beginTime ;
			if(mapPauseTime.containsKey(tag)&&0!=mapPauseTime.get(tag))
			{
				beginTime = mapPauseTime.get(tag);
			}
			else if(mapBeginTime.containsKey(tag)&&0!=mapBeginTime.get(tag))
			{
				beginTime = mapBeginTime.get(tag);
			}
			else
			{
				LogUtil.e("pause()前应该先调用begin()");
				return 0;
			}
			long endTime = System.currentTimeMillis();
			LogUtil.i(tip+":"+(endTime-beginTime)+"毫秒");
			return endTime-beginTime;
		}

		public static long stop(String tip)
		{
			return stop(tip,defTag);
		}
		public static long stop(String tip,int tag)
		{
			if(mapBeginTime.containsKey(tag)&&0!=mapBeginTime.get(tag))
			{
				long beginTime = mapBeginTime.get(tag);
				long endTime = System.currentTimeMillis();
				LogUtil.i(tip+":"+(endTime-beginTime)+"毫秒");
				mapBeginTime.put(tag, (long) 0);
				return endTime-beginTime;
			}
			else
			{
				LogUtil.e("在调用stop()前应该先调用begin()");
			}
			return 0;
		}
	}
	
	/**
	 * 从源文件的指定地方复制指定大小的字节到指定地方生成新文件
	 * @param position 源文件的指定起始位置
	 * @param size 源文件的指定
	 * @param inPath 源文件地址
	 * @param outPath 新文件地址
	 */
	public static void copyFile(long position, int size, String inPath, String outPath) {
		delFileIfExists(outPath);
		long sizeNow = 0;
		int sizeBase = DEF_SIZE_BUFF;
		while (sizeNow < size) {

			if (sizeNow + sizeBase > size) {
				sizeBase = (int) (size - sizeNow);
			}
			byte[] bytes = Reader.readFileToBytes(position, sizeBase, inPath);
			Writer.writeFileAtEnd(bytes, outPath);
			position += sizeBase;
			sizeNow += sizeBase;
		}
	}

	/**
	 * 快速复制文件
	 * @param inPath 输入文件路径
	 * @param outPath 输出文件路径
	 * @return
	 */
	public static boolean copyFile(@NonNull String inPath,@NonNull String outPath)
	{
		File inFile = new File(inPath);
		File outFile = new File(outPath);
		return copyFile(inFile,outFile);
	}

	public static boolean copyFile(@NonNull File inFile,@NonNull File outFile)
	{
		if(!inFile.exists())
		{
			return false;
		}
		FileChannel fileIn = null;
		FileChannel fileOut = null;
		try {
			fileIn = new FileInputStream(inFile).getChannel();
			fileOut = new FileOutputStream(outFile).getChannel();
			fileIn.transferTo(0, fileIn.size(), fileOut);
		} catch (IOException e) {
			LogUtil.exception(e);
			return false;
		} finally {
			CloseUtil.closeIO(fileIn, fileOut);
		}
		return true;
	}

	public static boolean copyFile(InputStream inputStream,String outPath)
    {
    	if(inputStream==null) return false;
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outPath);
            byte[] buffer = new byte[DEF_SIZE_BUFF];
            int byteCount;
            while((byteCount = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, byteCount);
            }
            fileOutputStream.flush();
            inputStream.close();
            fileOutputStream.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

	/**
	 * 在指定位置复写一个新的文件到一个文件里
	 * @param position 指定位置
	 * @param outFilePath 输出文件
	 * @param inFilePath 输入文件
	 * @return
	 */
	public static boolean coverFile(long position, String inFilePath, String outFilePath) {
		File inFile = new File(inFilePath);
		int sizeBuff = DEF_SIZE_BUFF;
		if (inFile.exists()) {
			try {
				RandomAccessFile randomAccessFile = new RandomAccessFile(outFilePath, "rw");
				InputStream in = new FileInputStream(inFile);
				if(sizeBuff>inFile.length())
				{
					sizeBuff = (int) inFile.length();
				}
				byte[] data = new byte[sizeBuff];
				int len ;
				while ((len = in.read(data)) > 0) {
					randomAccessFile.seek(position);
					randomAccessFile.write(data, 0, len);
					position += len;
				}
				in.close();
				randomAccessFile.close();
				return true;
			} catch (FileNotFoundException e) {
				LogUtil.exception(e);
			} catch (IOException e) {
				LogUtil.exception(e);
			}
		}
		return false;
	}

	/**
	 * 创建一个指定长度的空文件
	 * @param path
	 * @param size
	 */
	public static void createNullFile(String path,long size)
	{
		RandomAccessFile randomAccessFile = null;
		try {
			randomAccessFile = new RandomAccessFile(path, "rw");
			long position = 0 ;
			long nowSize = 0;
			int sizeBuff = DEF_SIZE_BUFF;
			while(nowSize<size)
			{
				if(nowSize+sizeBuff>size)
				{
					sizeBuff = (int) (size-nowSize) ;
				}
				byte[] bytesNull = new byte[sizeBuff];
				randomAccessFile.seek(position);
				randomAccessFile.write(bytesNull);
				position+=sizeBuff;
				nowSize+=sizeBuff;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if(randomAccessFile!=null)
					randomAccessFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 创建文件夹 
	 * @param path
	 */
	@SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
    public static void createFolder(String path)
	{
		if(path==null||path.equals(""))
		{
			return ;
		}
		File file = new File(path);
		String prefix = getPrefix(path);
		if(prefix==null)
		{
			file.mkdirs();
		}
		else
		{
			if(!file.exists())
			{
				int spandIndex = path.lastIndexOf("/");
				if(spandIndex!=-1)
				{
					String parentPath = path.substring(0,spandIndex);
					if(parentPath!=null)
					{
						File parentFile = new File(parentPath);
						parentFile.mkdirs();
					}
				}
			}
		}
	}

	/**
	 * 获取文件后缀名
	 * @param path 文件完整路径
	 * @return 如果获取失败返回null 否则返回后缀(mp3,jpg,png....)
	 */
	public static String getPrefix(String path)
	{
		int indexDot = path.lastIndexOf(".");
		if(indexDot!=-1)
		{
			if(path.length()>indexDot+1)
			{
				String prefix = path.substring(indexDot+1);
				if(!prefix.contains("/"))
				{
					if(prefix.contains("?"))
					{
						int indexY = prefix.indexOf("?");
						prefix = prefix.substring(0,indexY);
					}
					return prefix;
				}
			}
		}
	   return null;
	}

	@NonNull
	public static String formatUrl(@NonNull String url)
	{
		int index = url.indexOf("?");
		if(index>0)
		{
			return url.substring(0,index);
		}
		return url;
	}

	public static String getName(String path)
	{
		File file = new File(path);
		if(file.exists())
		{
			return file.getName();
		}
		return null;
	}

	public static boolean rename(File file,File newFile)
	{
		return file.renameTo(newFile);
	}
	
	@SuppressWarnings("SimplifiableIfStatement")
    public static boolean exists(String path)
	{
		if(path!=null&&!path.equals(""))
		{
			return new File(path).exists();
		}
		return false;
	}


}
