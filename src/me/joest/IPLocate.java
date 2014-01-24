package me.joest;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

/**
 * 基于平衡二叉搜索树的IP定位主要类
 * @author joest
 *
 */
public class IPLocate {

	private Map<String, LocateResult> ipcache;
	private SearchTree st;
	
	private static IPLocate instance;
	
	private IPLocate()
	{
	}
	
	/**
	 * 使用单一模式获得IPLocate实例
	 * @return IPLocate实例
	 */
	public static IPLocate getInstance()
	{
		if(instance == null)
		{
			instance = new IPLocate();
			instance.init();
		}
		return instance;
	}
	
	public static void destroy()
	{
		if(instance != null)
			instance = null;
	}
	
	/**
	 * 如果已经存在序列化文件，则读取建立搜索树，如果不存在则执行build()建立搜索树，然后使用serialTree序列化对象
	 * @exception FileNotFoundException 序列化文件不存在
	 * @exception IOException 读取序列化文件出错
	 * @exception ClassNotFoundException 搜索树对象找不到
	 */
	private void init()
	{
		String fn = "ips.dat";
		try{
			SearchTree st;
			ObjectInputStream oin = new ObjectInputStream(new FileInputStream(fn));
			st = (SearchTree)oin.readObject();
			this.st = st;
			this.ipcache = new HashMap<String, LocateResult>();
		} catch(FileNotFoundException e) {
			System.out.println("没有找到序列化文件, 先使用IPLocate.build(String ipfile)创建.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 序列化搜索树到文件
	 * @param fn 序列化对象到指定的文件名
	 * @exception FileNotFoundException 序列化对象目标文件不存在
	 * @exception IOException 文件读写出错
	 * @exception NotSerializableException 不能序列化该对象
	 */
	public static void serialTree(SearchTree st)
	{
		String fn = "ips.dat";
		ObjectOutputStream oout;
		try {
			oout = new ObjectOutputStream(new FileOutputStream(fn));
			oout.writeObject(st);
			oout.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotSerializableException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	public static void build(String filename)
	{
		System.out.println("正在创建序列化文件...");
		String ff = filename;
		String line;
		String[] values;
		SearchTree st = new SearchTree();
		try {
			BufferedReader in = new BufferedReader(new FileReader(ff));
			while((line = in.readLine()) != null)
			{
				values = line.split("\t");
				IPEntry ipe = new IPEntry(values[0], values[1], values[2], values[3],
										  values[4], values[5]);
				st.insert(ipe);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		serialTree(st);
	}
	
	
	public LocateResult locate(String ip)
	{
		LocateResult lr;
		if(!isIP(ip))
		{
			System.out.println("查询的IP不符合规范.");
			return null;
		}
		
		if(this.ipcache.containsKey(ip))
		{
			lr = this.ipcache.get(ip);
			return lr;
		}
		
		SearchTree tree = this.st;
		BinaryNode node = tree.search(ip);
		if(node == null)
		{
			this.ipcache.put(ip, new LocateResult(ip));
			return null;
		}
		
		IPEntry ipe = node.data;
		if(ipe.contains(ip))
		{
			lr = new LocateResult(ipe, ip);
			this.ipcache.put(ip, lr);
			return lr;
		}
		else
		{
			this.ipcache.put(ip, new LocateResult(ip));
			return null;
		}
	}
	
	/**
	 * 清除缓存
	 */
	public void clear()
	{
		this.ipcache.clear();
	}
	
	/**
	 * 检查要查询的IP是否符合IP的格式
	 * @param ip 所要查询的IP
	 * @return 如果符合返回true，否则false
	 */
	public boolean isIP(String ip)
	{
		String[] parts = ip.split("\\.");
		if(parts.length != 4)
			return false;
		
		for(int i=0;i<4;i++)
		{
			try
			{
				int value = Integer.parseInt(parts[i]);
				if(value<0 || value > 255)
					return false;
			} catch(NumberFormatException e){
				return false;
			}
		}
		return true;
	}
	
	public static void main(String[]args)
	{
		String ipfile = "outipall.ip";
		IPLocate.build(ipfile);
		IPLocate iplocate = IPLocate.getInstance();
		
		String ip = "0.0.0.1";
		LocateResult lr = iplocate.locate(ip);
		System.out.println(lr);
	}
	
	/**
	 * 对于查询IP返回的结果类
	 * @author joest
	 *
	 */
	public class LocateResult extends Info{
		
		private String searchIp;
		
		public LocateResult()
		{	
			super();
			this.searchIp = "--";
		}
		
		public LocateResult(String ip)
		{
			super();
			this.searchIp = ip;
		}
		
		public LocateResult(String _country, String _province, String _isp, String _department,
							String _searchIp)
		{
			super(_country, _province, _isp, _department);
			this.searchIp = _searchIp;
		}
		
		public LocateResult(IPEntry ipe, String searchIp)
		{
			this(ipe.getCountry(), ipe.getProvince(), ipe.getIsp(), ipe.getDepartment(), searchIp);
		}
		
		public String toString()
		{
			String formatter = "[%s]\t%s\t%s\t%s\t%s";
			return String.format(formatter, this.searchIp, super.getCountry(), super.getProvince(), 
					super.getIsp(), super.getDepartment());
		}
		
		public boolean isInChina()
		{
			String china = "境内";
			if (china.compareTo(super.getCountry()) == 0)
				return true;
			return false;
		}

		public String getSearchIp() {
			return searchIp;
		}
	}
}

/**
 * 根据需要提取出来的父类，包含IP对应的相关位置信息
 * @author joest
 *
 */
class Info implements Serializable{
	private String country;
	private String province;
	private String isp;
	private String department;
	
	public Info()
	{
		String ddfault = "--";
		this.country = ddfault;
		this.province = ddfault;
		this.isp = ddfault;
		this.department = ddfault;
	}
	
	public Info(String _country, String _province, String _isp, String _department)
	{
		this.country = _country;
		this.province = _province;
		this.isp = _isp;
		this.department = _department;
	}
	
	public String toString()
	{
		String formatter = "%s, %s, %s, %s";
		return String.format(formatter, this.country, this.province, this.isp, this.department);
	}

	public String getCountry() {
		return country;
	}

	public String getProvince() {
		return province;
	}

	public String getIsp() {
		return isp;
	}

	public String getDepartment() {
		return department;
	}

}
