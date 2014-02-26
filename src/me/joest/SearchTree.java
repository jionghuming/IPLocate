package me.joest;

import java.io.Serializable;

class Status {

	final static int BIGTHAN = 1;  // 被插入的节点小
	final static int SMALLTHAN = 2; // 被插入的节点大
	final static int CONTAIN = 3; // 被插入的节点为被包含节点或者查询的IP在节点范围内
	final static int CONTAINED = 4;   //被插入的节点为包含节点
	final static int OTHER = 5;
}

/**
 * IP区间的位置信息
 * @author joest
 *
 */
class IPEntry extends Info implements Serializable{

	private static final long serialVersionUID = 7068795334538599126L;

	private String beginIp;
	private String endIp;
	
	public IPEntry()
	{
		super();
		this.beginIp = "--";
		this.endIp = "--";
	}
	
	public IPEntry(String _beginIp, String _endIp, String _country, String _province,
				   String _isp, String _department)
	{
		super(_country, _province, _isp, _department);
		this.beginIp = _beginIp;
		this.endIp = _endIp;
	}
	
	public int compareTo(IPEntry p)
	{
		String a1 = this.beginIp;
		String b1 = this.endIp;
		String a2 = p.beginIp;
		String b2 = p.endIp;
		
		int a1b2 = ipCompare(a1, b2);
		int b1a2 = ipCompare(b1, a2);
		
		int a1a2 = ipCompare(a1, a2);
		int b1b2 = ipCompare(b1, b2);
		
		
		// 1.不相交,且被插入的节点较小
		if(a1b2>0)
			return Status.BIGTHAN;
		
		// 1.不相交，且被插入的节点较大
		if(b1a2<0)
			return Status.SMALLTHAN;
		
		// 2.包含，且被插入的节点为被包含节点
		if(a1a2<=0 && b1b2>=0)
			return Status.CONTAIN;
			
		// 2.包含，且被插入的节点为包含节点
		if(a1a2>=0 && b1b2<=0)
			return Status.CONTAINED;
			
		// 3.相交，且被插入的节点后交
		if(a1a2<0 && b1a2>=0 && b1b2<0)
		{
			p.beginIp = plusone(b1);
			return Status.SMALLTHAN;
		}
		
		// 3.相交，且被插入的节点前交
		if(a1a2>0 && a1b2<=0 && b1b2>0)
		{
			p.endIp = minusone(a1);
			return Status.BIGTHAN;
		}
		
		return Status.OTHER;
	}
	
	public int compareIP(String ip)
	{
		String beginip = this.beginIp;
		String endip = this.endIp;
		
		if(this.ipCompare(ip, endip)>0)
			return Status.SMALLTHAN;
		else if(this.ipCompare(ip, beginip)<0)
			return Status.BIGTHAN;
		else
			return Status.CONTAIN;
	}
	
	/**
	 * 比较两个IP大小
	 * @param ip1
	 * @param ip2
	 * @return negtive if ip1 < ip2，positive if ip1 > ip2, else 0
	 */
	private int ipCompare(String ip1, String ip2)
	{
		String[] ips1 = ip1.split("\\.");
		String[] ips2 = ip2.split("\\.");
		int tempa, tempb;
		
		for(int i=0;i<4;i++)
		{
			tempa = Integer.parseInt(ips1[i]);
			tempb = Integer.parseInt(ips2[i]);
			if(tempa != tempb)
				return tempa - tempb;
			else
				continue;
		}
		// 相同
		return 0;
	}
	
	/**
	 * 将ip加1操作
	 * @param ip
	 * @return 结果ip
	 */
	public String plusone(String ip)
	{
		int i;
		String format="%s.%s.%s.%s";
		String ccip;
		String[] ipss = ip.split("\\.");
		int [] ips = new int[4];
		
		for(i=0;i<4;i++)
			ips[i] = Integer.parseInt(ipss[i]);
		
		i--;
		ips[i] += 1;
		while(ips[i]>255)
		{
			ips[i] -= 256;
			i--;
			ips[i] += 1;
		}
		
		ccip = String.format(format, String.valueOf(ips[0]), String.valueOf(ips[1]), 
				   String.valueOf(ips[2]), String.valueOf(ips[3]));
		return ccip;
	}
	
	/**
	 * 将ip减1操作
	 * @param ip
	 * @return 结果ip
	 */
	public String minusone(String ip)
	{
		int i;
		String format="%s.%s.%s.%s";
		String ccip;
		String[] ipss = ip.split("\\.");
		int [] ips = new int[4];
		
		for(i=0;i<4;i++)
			ips[i] = Integer.parseInt(ipss[i]);
		
		i--;
		ips[i] -= 1;
		while(ips[i]<0)
		{
			ips[i] += 256;
			i--;
			ips[i] -= 1;
		}
		
		ccip = String.format(format, String.valueOf(ips[0]), String.valueOf(ips[1]), 
				   String.valueOf(ips[2]), String.valueOf(ips[3]));
		return ccip;
	}
	
	
	public boolean contains(String ip)
	{
		if(ipCompare(ip, this.beginIp)>=0 && ipCompare(ip, this.endIp)<=0)
			return true;
		return false;
	}
	
	public String toString()
	{
		String formatter = "%s - %s: %s";
		return String.format(formatter, this.beginIp, this.endIp, super.toString());
	}
}

class BinaryNode implements Serializable
{
	private static final long serialVersionUID = -2212988863253660871L;
	public IPEntry data;
	public BinaryNode left;
	public BinaryNode right;
	public BinaryNode subnode;
	public int height;
	
	public BinaryNode()
	{
		this.data = null;
		this.left = null;
		this.right = null;
		this.subnode = null;
		this.height = 0;
	}
	
	public BinaryNode(IPEntry ip)
	{
		this(ip, null, null, null, 0);
	}
	
	public BinaryNode(IPEntry r, BinaryNode left, BinaryNode right, BinaryNode subnode, int height)
	{
		this.data = r;
		this.left = left;
		this.right = right;
		this.subnode = subnode;
		this.height = height;
	}
	
	public int compareTo(BinaryNode b)
	{
		return this.data.compareTo(b.data);
	}
	
	public int compareIP(String ip)
	{
		return this.data.compareIP(ip);
	}
	
	public String toString()
	{
		String format = "%s, %s";
		return String.format(format, this.data.toString(), this.height);
	}
}


public class SearchTree implements Serializable{

	private static final long serialVersionUID = -7378985533751230121L;
	private BinaryNode root;
	private int nodeNum;
	
	public SearchTree()
	{
		this.root = null;
		this.nodeNum = 0;
	}
	
	public int height(BinaryNode node)
	{
		if(node == null)
			return -1;
		return node.height;
	}
	
	public void createTree(BinaryNode node)
	{
		this.root = node;
		this.nodeNum++;
	}
	
	
	public void insert(IPEntry ip)
	{
		BinaryNode newnode = new BinaryNode(ip);
		this.insert(newnode);
	}
	
	public void insert(BinaryNode node)
	{
		if(this.root == null)
			createTree(node);
		else
		{
			this.root = insert(node, this.root);
			this.nodeNum++;
		}
	}

	
	/**
	 * 将新节点插入到以p为根节点的子树
	 * @param 子树的根节点
	 * @param 需要插入的新节点
	 * @return 插入新节点后，新节点的父节点
	 */
	private BinaryNode insert(BinaryNode newnode, BinaryNode p)
	{
		if(p == null)
			return newnode;
		if(p.compareTo(newnode) == Status.BIGTHAN)
		{
			p.left = this.insert(newnode, p.left);
			if(height(p.left) - height(p.right) == 2)
			{
				if(newnode.compareTo(p.left)<0)
					p = rotateWithLeftChild(p);
				else
					p = rotateWithLRChild(p);
			}
		}
		else if(p.compareTo(newnode) == Status.SMALLTHAN) //right
		{
			p.right = this.insert(newnode, p.right);
			if(height(p.right) - height(p.left) == 2)
			{
				if(newnode.compareTo(p.right)<0)
				{
					p = rotateWithRLChild(p);   // 
				}
				else
					p = rotateWithRightChild(p);
			}
		}
		else if(p.compareTo(newnode) == Status.CONTAIN)
		{
			BinaryNode pp = p;
			while(pp.subnode != null)
				pp = pp.subnode;
			pp.subnode = newnode;
		}
		else if(p.compareTo(newnode) == Status.CONTAINED)
		{
			changeData(p, newnode);
		    BinaryNode pp = p;
		    while(pp.subnode != null)
		    	pp = pp.subnode;
		    pp.subnode = newnode;
		}
		else
			;	//OTHER
		p.height = Math.max(height(p.left), height(p.right)) + 1;
		return p;
	}
	
	/**
	 * 当目标节点包含需要插入的节点时，交换节点的数据
	 * @param p
	 * @param newnode
	 */
	public void changeData(BinaryNode p, BinaryNode newnode)
	{
		IPEntry left = p.left.data;
		IPEntry right = p.right.data;
		IPEntry newEntry;
		
		left.compareTo(newnode.data);
		right.compareTo(newnode.data);
		
		//switch
		newEntry = newnode.data;
		newnode.data = p.data;
		p.data = newEntry;
	}
	
	
	//LL
	public BinaryNode rotateWithLeftChild(BinaryNode node)
	{
		BinaryNode k1 = node.left;
		node.left = k1.right;
		k1.right = node;
		
		node.height = Math.max(height(node.left), height(node.right)) + 1;
		k1.height = Math.max(height(k1.left), height(k1.right)) + 1;
		return k1;
	}
	
	//RR
	public BinaryNode rotateWithRightChild(BinaryNode node)
	{
		BinaryNode k1 = node.right;
		node.right = k1.left;
		k1.left = node;
		
		node.height = Math.max(height(node.left), height(node.right)) + 1;
		k1.height = Math.max(height(k1.left), height(k1.right)) + 1;
		return k1;
	}
	
	//LR
	public BinaryNode rotateWithLRChild(BinaryNode node)
	{
		BinaryNode k1 = node.left;
		BinaryNode k2 = k1.right;
		k1.right = k2.left;
		node.left = k2;
		k2.left = k1;
		
		k1.height = Math.max(height(k1.left), height(k1.right)) + 1;
		k2.height = Math.max(height(k2.left), height(k2.right)) + 1;
		
		return rotateWithLeftChild(node);
	}
	
	//RL
	public BinaryNode rotateWithRLChild(BinaryNode node)
	{
		BinaryNode k1 = node.right;
		BinaryNode k2 = k1.left;
		k1.left = k2.right;
		k2.right = k1;
		node.right = k2;
		
		k1.height = Math.max(height(k1.left), height(k2.right));
		k2.height = Math.max(height(k2.left), height(k2.right));
		
		return rotateWithRightChild(node);
	}

	public BinaryNode search(String key)
	{
		BinaryNode pp, parent, min;
		min = parent = pp = this.root;
		if(key == null)
			return null;
		while(pp != null)
		{
			if(pp.compareIP(key) == Status.SMALLTHAN)
			{
				min = pp;
				parent = pp;
				pp = pp.right;
			}
			else if(pp.compareIP(key) == Status.BIGTHAN)
			{
				parent = pp;
				pp = pp.left;
			}
			else if(pp.compareIP(key) == Status.CONTAIN)
			{
				BinaryNode deep = pp;
				while(deep.subnode != null && deep.subnode.compareIP(key) == Status.CONTAIN)
					deep = pp.subnode;
				return deep;
			}
		}
		return null;
	}

	public void printTree()
	{
		this.printTree(this.root);
	}
	
	private void printTree(BinaryNode node)
	{
		if(node != null)
		{
			this.printTree(node.left);
			System.out.println(node.toString());
			this.printTree(node.right);
		}
	}
	
	public static void main(String[] args)
	{
		//TODO
	}
}

