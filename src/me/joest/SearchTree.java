package me.joest;

import java.io.Serializable;

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
	
	public IPEntry(String _beginIp)
	{
		this.beginIp = _beginIp;
	}
	
	public int compareTo(IPEntry p)
	{
		return ipCompare(this.beginIp, p.beginIp);
	}
	
	
	/**
	 * 比较两个IP大小
	 * @param ip1
	 * @param ip2
	 * @return negtive if ip1 < ip2，positive if ip1 > ip2, else 0
	 */
	public int ipCompare(String ip1, String ip2)
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

	public String getBeginIp() {
		return beginIp;
	}

	public void setBeginIp(String beginIp) {
		this.beginIp = beginIp;
	}
}

class BinaryNode implements Serializable
{
	private static final long serialVersionUID = -2212988863253660871L;
	public IPEntry data;
	public BinaryNode left;
	public BinaryNode right;
	public int height;
	
	public BinaryNode()
	{
		this.data = null;
		this.left = null;
		this.right = null;
		this.height = 0;
	}
	
	public BinaryNode(IPEntry ip)
	{
		this(ip, null, null, 0);
	}
	
	public BinaryNode(IPEntry r, BinaryNode left, BinaryNode right, int height)
	{
		this.data = r;
		this.left = left;
		this.right = right;
		this.height = height;
	}
	
	public int compareTo(BinaryNode b)
	{
		return this.data.compareTo(b.data);
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
	 * 
	 * @param p
	 * @param newnode
	 * @return parent node of newnode
	 */
	private BinaryNode insert(BinaryNode newnode, BinaryNode p)
	{
		if(p == null)
			return newnode;
		if(p.compareTo(newnode)>0)
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
		else if(p.compareTo(newnode)<0) //right
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
		else
			;	//
		p.height = Math.max(height(p.left), height(p.right)) + 1;
		return p;
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

	public BinaryNode search(BinaryNode key)
	{
		BinaryNode pp, parent, min;
		min = parent = pp = this.root;
		if(key == null)
			return null;
		while(pp != null)
		{
			if(pp.compareTo(key)<0)
			{
				min = pp;
				parent = pp;
				pp = pp.right;
			}
			else if(pp.compareTo(key)>0)
			{
				parent = pp;
				pp = pp.left;
			}
			else
				return pp;
		}
		if(parent.compareTo(key)>0)
			return min;
		return parent;
	}

	public BinaryNode search(String ip)
	{
		IPEntry ipe = new IPEntry(ip);
		BinaryNode node;
		node = this.search(new BinaryNode(ipe));
		return node;
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
		
	}
}
