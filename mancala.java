//Aishwarya Desai Assignment 2
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class State implements Comparable<State>
{
	String name;
	int value;
	int alpha_value;
	int beta_value;
	board_state b;
	State parent;
	String type;
	int depth;
	
	@Override
	public int compareTo(State s) {
		// TODO Auto-generated method stub
		if(this.value==s.value)
		{
			return name.compareTo(s.name);
		}
		else
			return s.value-value;
	}

	public String print()
	{
		if(value==Integer.MAX_VALUE)
		{
			return "Infinity";
		}
		if(value==Integer.MIN_VALUE)
			return "-Infinity";
		return value+"";
	}
	@Override
	public String toString() {
		//return "State: [name=" + name + ", v=" + value + ", "+" b=" + b + ", "+ ", type=" + type + ", depth=" + depth + "]";
	}
	
	
}



public class mancala {
	
	BufferedWriter traverse_log;
	static int myPlayer;
	static int cutOff;
	
	public static void main(String args[])
	{
		System.out.println("in main");
		BufferedReader input_buff=null;
		BufferedWriter next_move=null;
		try 
		{
			input_buff=new BufferedReader(new FileReader(args[1]));
		
			Pit_class pits[];
			int algo=Integer.parseInt(input_buff.readLine());
			myPlayer=Integer.parseInt(input_buff.readLine());
			cutOff=Integer.parseInt(input_buff.readLine());
			String P2state=input_buff.readLine();
			String stones[]=P2state.split(" ");
			int pits_num=stones.length*2+2+1;
			pits=new Pit_class[pits_num];
			int playerPits=stones.length;
			
			int i,j;
			j=pits_num-1;
			int k=2;
			for(i=0;i<stones.length;j--,i++)
			{
				pits[j]=new Pit_class();
				pits[j].stones_count=Integer.parseInt(stones[i]);
				pits[j].name="A"+k;
				k++;
			}
			String P1state=input_buff.readLine();
			stones=P1state.split(" ");
			
			for(i=0,j=2;i<stones.length;j++,i++)
			{
				System.out.println("index:"+i);
				pits[j]=new Pit_class();
				pits[j].stones_count=Integer.parseInt(stones[i]);
				pits[j].name="B"+j;
			}
			pits[1]=new Pit_class();
			pits[1].stones_count=Integer.parseInt(input_buff.readLine());
			pits[1].name="A1";
			pits[playerPits+2]=new Pit_class();
			pits[playerPits+2].stones_count=Integer.parseInt(input_buff.readLine());
			pits[playerPits+2].name="B"+(playerPits+2);
			
			board_state Board=new board_state(pits, pits_num-1, playerPits, myPlayer);
	
			mancala manc=new mancala();
			
			if(algo==1)
			{
				System.out.println("in greedy algo");
				manc.greedy_decision(Board);
			}
			else if(algo==2)
			{
				System.out.println("in minmax algo");
				State s=new State();
				s.b=Board;		
				s.parent=null;
				s.value=Integer.MIN_VALUE;
				s.name="root";
				s.depth=0;
				s.type="MAX";
				s.parent=null;
				manc.traverse_log=new BufferedWriter(new FileWriter("traverse_log.txt"));
				try {
					
					manc.traverse_log.write("Node,Depth,Value\r\n");
					
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			
				manc.MinimaxDecision(s);
			}
			else if(algo==3)
			{
				System.out.println("in alpha beta");
				State s=new State();
				s.b=Board;		
				s.parent=null;
				s.value=Integer.MIN_VALUE;
				s.name="root";
				s.depth=0;
				s.type="MAX";
				s.parent=null;
				s.alpha_value=Integer.MIN_VALUE;
				s.beta_value=Integer.MAX_VALUE;
				manc.traverse_log=new BufferedWriter(new FileWriter("traverse_log.txt"));
				try {
						manc.traverse_log.write("Node,Depth,Value,Alpha,Beta\r\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			
				manc.temp_Tree=new ArrayList<State>();
				int value=manc.MaxValue_alpha_beta(s,Integer.MIN_VALUE,Integer.MAX_VALUE);
				System.out.println(value);
				State s1=manc.finalMove(s,value);
				System.out.println(s1);
				manc.printNextMove(s1.b);
		
			}
		}
		catch(IOException e)
		{
			System.out.println("Exception: "+e);
		}
	}

	
	
	public void greedy_decision(board_state B)
	{
		
		State s=generateSucc(B);
		
		printNextMove(s.b);
	}
	
		
	
	public State generateSucc(board_state b)
	{
		State s = null;
		List<State> children=new ArrayList<State>();
		int i,j;
		for(i=b.myStartPit();i<=b.OppMancala+b.playerPits;i++)
		{
			
			if(b.my_Pits[i].stones_count==0)
				continue;
			s=new State();
			board_state child= null;
			try 
			{
					child = (board_state)b.clone();
					if(child.play1(i))
					{			
						s=generateSucc(child);
						
					}
					else
					{	
						int eval=child.calculate_eval();
						s.b=child;
						s.name=b.my_Pits[i].name;
						s.value=eval;	
					}
					children.add(s);
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Collections.sort(children);
		return children.get(0);
	}
	
	List<State> temp_Tree;
	public void MinimaxDecision(State s)
	{
		temp_Tree=new ArrayList<State>();
		int v=MaxValue(s);
		System.out.println(v);
		
		State s1=finalMove(s, v);
		printNextMove(s1.b);
	
	}
	public boolean terminate_condition(State state)
	{
		if(state.b.allOppPitsEmpty() && state.b.allMyPitsempty())
			return true;
		return false;
	}
	
	public int MaxValue(State s1)
	{
		board_state b=s1.b;
		State s;
		int v=Integer.MIN_VALUE;
		if(myPlayer==1)
		{
			if(terminate_condition(s1))
			{
				
				
				if(s1.depth!=cutOff || (s1.depth==cutOff && s1.type.equals(s1.parent.type)))
				{
					try {
						
						traverse_log.write(s1.name+","+s1.depth+","+get_v(v)+"\r\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}
				s1.value=s1.b.calculate_eval();
				try {
					traverse_log.write(s1.name+","+s1.depth+","+get_v(s1.value)+"\r\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(s1.depth==1)
					temp_Tree.add(s1);
				return s1.value;
			}

			
			s1.value=v;
			try 
			{
			
				traverse_log.write(s1.name+","+s1.depth+","+s1.print()+"\r\n");
			} 
			catch (IOException e) 
			{
			
				e.printStackTrace();
			}
			
			
			for(int i=b.myStartPit(); i<=b.OppMancala+b.playerPits ;i++)
			{
				
				if(b.my_Pits[i].stones_count==0)
				{
					continue;
				}
				s=new State();
				board_state child= null;
				try 
				{
						child = (board_state)b.clone();		
						boolean flag=child.play1(i);
						s.b=child;
						s.name=b.my_Pits[i].name;
			
						System.out.println("Child:"+s.name+child);
						s.parent=s1;
						if(s1.type.equals("MAX"))
							s.depth=s1.depth+1;
						else
							s.depth=s1.depth;
						s.type="MIN";	
						if(flag)
						{	
							v=Math.max(v,MaxValue(s));
							s1.value=v;	
							try 
							{
								//System.out.println("MAX: "+s.name+","+s.depth+","+s.print()+"\r\n");
								traverse_log.write(s1.name+","+s1.depth+","+s1.print()+"\r\n");
							} 
							catch (IOException e) 
							{
							// TODO Auto-generated catch block
								e.printStackTrace();
							}		
						}
						else
						{
							if(s.depth==cutOff)
							{
									s.value=s.b.calculate_eval();
									try {
										traverse_log.write(s.name+","+s.depth+","+get_v(s.value)+"\r\n");
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									v=Math.max(v, s.value);
									s1.value=v;
									
									if(s.depth==1)
										temp_Tree.add(s);
									try {
										traverse_log.write(s1.name+","+s1.depth+","+get_v(s1.value)+"\r\n");
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
							}
			
							else
							{
								v=Math.max(v,MinValue(s));
								s1.value=v;
								try 
								{
									traverse_log.write(s1.name+","+s1.depth+","+s1.print()+"\r\n");
								} 
								catch (IOException e) 
								{
							
								e.printStackTrace();
								}
							}
						}	
				} 
				catch (CloneNotSupportedException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(s1.depth==1)
				temp_Tree.add(s1);
			return v;

		}
		else
		{
			if(terminate_condition(s1))
			{
				
				
				if(s1.depth!=cutOff || (s1.depth==cutOff && s1.type.equals(s1.parent.type)))
				{
					try {
						traverse_log.write(s1.name+","+s1.depth+","+get_v(v)+"\r\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}
				s1.value=s1.b.calculate_eval();
				try {
					traverse_log.write(s1.name+","+s1.depth+","+get_v(s1.value)+"\r\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(s1.depth==1)
					temp_Tree.add(s1);
				return s1.value;
			}

			
			s1.value=v;
			try 
			{
			
				traverse_log.write(s1.name+","+s1.depth+","+s1.print()+"\r\n");
			} 
			catch (IOException e) 
			{
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			for(int i=(s1.b.size); i>s1.b.OppMancala;i--)
			{
				
				if(b.my_Pits[i].stones_count==0)
				{
					continue;
				}
				s=new State();
				board_state child= null;
				try 
				{
						child = (board_state)b.clone();
						
						boolean flag=child.play1(i);
						s.b=child;
						s.name=b.my_Pits[i].name;
						
						s.parent=s1;
						if(s1.type.equals("MAX"))
							s.depth=s1.depth+1;
						else
							s.depth=s1.depth;
						s.type="MIN";	
						if(flag)
						{	
							v=Math.max(v,MaxValue(s));
							s1.value=v;	
							try 
							{
			
								traverse_log.write(s1.name+","+s1.depth+","+s1.print()+"\r\n");
							} 
							catch (IOException e) 
							{
							// TODO Auto-generated catch block
								e.printStackTrace();
							}
						
						}
						else
						{
							if(s.depth==cutOff)
							{
									s.value=s.b.calculate_eval();
									try {
										traverse_log.write(s.name+","+s.depth+","+get_v(s.value)+"\r\n");
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									v=Math.max(v, s.value);
									s1.value=v;
									if(s.depth==1)
										temp_Tree.add(s);
									
									try {
										traverse_log.write(s1.name+","+s1.depth+","+get_v(s1.value)+"\r\n");
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
							}
							else
							{
								v=Math.max(v,MinValue(s));
								s1.value=v;
								try 
								{
			
									traverse_log.write(s1.name+","+s1.depth+","+s1.print()+"\r\n");
								} 
								catch (IOException e) 
								{
							// TODO Auto-generated catch block
								e.printStackTrace();
								}
							}
						}	
				} 
				catch (CloneNotSupportedException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(s1.depth==1)
				temp_Tree.add(s1);
			return v;

		}
		
	}
	
	public int MinValue(State s1)
	{
		board_state b=s1.b;
		State s;
		int v=Integer.MAX_VALUE;
		if(myPlayer==1)
		{
			if(terminate_condition(s1))
			{
				
				
				if(s1.depth!=cutOff || (s1.depth==cutOff && s1.type.equals(s1.parent.type)))
				{
					try {
						traverse_log.write(s1.name+","+s1.depth+","+get_v(v)+"\r\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}
				s1.value=s1.b.calculate_eval();
				try {
					traverse_log.write(s1.name+","+s1.depth+","+get_v(s1.value)+"\r\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(s1.depth==1)
					temp_Tree.add(s1);
				return s1.value;
			}

		
			//
			s1.value=v;
			try {
				traverse_log.write(s1.name+","+s1.depth+","+get_v(s1.value)+"\r\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			for(int i=b.size;i>b.MyMancala ;i--)
			{
				if(b.my_Pits[i].stones_count==0)
				{
					continue;
				}
				s=new State();
				board_state child= null;
				try 
				{
						child = (board_state)b.clone();
						boolean flag=child.play2(i);
						s.b=child;
						s.name=b.my_Pits[i].name;
						s.parent=s1;
						if(s1.type.equals("MIN"))
							s.depth=s1.depth+1;
						else
							s.depth=s1.depth;
						s.type="MAX";
						
			
						if(flag)
						{	
							v=Math.min(v,MinValue(s));
							s1.value=v;
							try {
								traverse_log.write(s1.name+","+s1.depth+","+get_v(s1.value)+"\r\n");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else
						{
							if(s.depth==cutOff)
							{
								s.value=s.b.calculate_eval();
								try {
									traverse_log.write(s.name+","+s.depth+","+get_v(s.value)+"\r\n");
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								v=Math.min(v, s.value);	
								
								s1.value=v;
								if(s.depth==1)
									temp_Tree.add(s);
									try {
										traverse_log.write(s1.name+","+s1.depth+","+get_v(s1.value)+"\r\n");
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
															
							}
							else
							{	
								v=Math.min(v,MaxValue(s));
								s1.value=v;
								try {
									traverse_log.write(s1.name+","+s1.depth+","+get_v(s1.value)+"\r\n");
								}
								catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								}
							}
						}		
				} 
				catch (CloneNotSupportedException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(s1.depth==1)
				temp_Tree.add(s1);
			return v;
	
		}
		else
		{
			if(terminate_condition(s1))
			{
				if(s1.depth!=cutOff || (s1.depth==cutOff && s1.type.equals(s1.parent.type)))
				{
					try {
						traverse_log.write(s1.name+","+s1.depth+","+get_v(v)+"\r\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}
				
				s1.value=s1.b.calculate_eval();
				try {
					traverse_log.write(s1.name+","+s1.depth+","+get_v(s1.value)+"\r\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(s1.depth==1)
					temp_Tree.add(s1);
				return s1.value;
			}

		
	
			s1.value=v;
			try {
				traverse_log.write(s1.name+","+s1.depth+","+get_v(s1.value)+"\r\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(int i=b.otherStartPit();i<=b.MyMancala+b.playerPits ;i++)
			{
				if(b.my_Pits[i].stones_count==0)
				{
					continue;
				}
				s=new State();
				board_state child= null;
				try 
				{
						child = (board_state)b.clone();
						boolean flag=child.play2(i);
						s.b=child;
						s.name=b.my_Pits[i].name;
						s.parent=s1;
						if(s1.type.equals("MIN"))
							s.depth=s1.depth+1;
						else
							s.depth=s1.depth;
						s.type="MAX";
						if(flag)
						{	
							
							v=Math.min(v,MinValue(s));
							s1.value=v;
							try {
								traverse_log.write(s1.name+","+s1.depth+","+get_v(s1.value)+"\r\n");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else
						{
							if(s.depth==cutOff)
							{
								s.value=s.b.calculate_eval();
								try {
									traverse_log.write(s.name+","+s.depth+","+get_v(s.value)+"\r\n");
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								v=Math.min(v, s.value);	
								 
								s1.value=v;
								if(s.depth==1)
									temp_Tree.add(s);
									try {
										traverse_log.write(s1.name+","+s1.depth+","+get_v(s1.value)+"\r\n");
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}								
							}
							else
							{
								v=Math.min(v,MaxValue(s));
								s1.value=v;
								try {
									traverse_log.write(s1.name+","+s1.depth+","+get_v(s1.value)+"\r\n");
								}
								catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								}
							}
						}		
				} 
				catch (CloneNotSupportedException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(s1.depth==1)
				temp_Tree.add(s1);
			return v;
	
		}
	}
	
	
	
	
	
	public void printNextMove(board_state b)
	{
		BufferedWriter out=null;
		try 
		{
			out=new BufferedWriter(new FileWriter("next_state.txt"));
			Pit_class[] Pits=b.my_Pits;
			for(int i=b.size;i>b.size-b.playerPits;i--)
				out.write(Pits[i].stones_count+" ");
			out.write("\r\n");
			for(int i=2;i<=b.playerPits+1;i++)
				out.write(Pits[i].stones_count+" ");
			out.write("\r\n"+Pits[1].stones_count);
			int x=b.playerPits+2;
			
			out.write("\r\n"+Pits[x].stones_count+"\r\n");
		}
		catch(IOException e)
		{
			
		}
		finally
		{
		try{
				if(out!=null)
				{
					out.close();
				}
				if(traverse_log!=null)
				{
					traverse_log.close();
				}
			}
			catch(IOException e)
			{
		 		e.printStackTrace();
			}
		}
	}	
	

	public State end_pos(State s,int value)
	{
		State f=null;
		List<State> successors=Successors(s);
		if(successors!=null)
		{
			if(successors.get(0).type.equals("MIN"))
			{
				for(State state: successors)
				{
					if(state.value==value)
					{
						f=finalMove(state, value);
						return f;
					}
				}
			}
		}
		else
		if((s.value)==value)
		{
			return s;
		}
		return s;
	}
	
	
	class sort_Alpabetically implements Comparator<State>
	{
		@Override
		public int compare(State arg1, State arg2) {
			// TODO Auto-generated method stub
			return (arg1.name).compareTo(arg2.name);
		}
	}
	
	public State finalMove(State s,int value)
	{
		State f=null;
		List<State> successors=Successors(s);
		if(successors!=null)
		{
			if(successors.get(0).type.equals("MIN"))
			{
				for(State state: successors)
				{
					if(state.value==value)
					{
			
						f=finalMove(state, value);
						return f;
					}
				}
				
			}
		}
		else
		if((s.value)==value)
		{
			System.out.println("Inside this");
			return s;
		}
		return s;
	}
	

	public List<State> Successors(State s)
	{
		List<State> children=new ArrayList<State>();
		boolean flag=false;
		
		for(State child:temp_Tree)
		{
			if(child.parent!=null && child.parent==s)
			{
				children.add(child);
				flag=true;
			}
		}
		if(flag==true)
		{
			Collections.sort(children, new sort_Alpabetically());
			return children;
		}
		return null;
	}
	public boolean isCutoff(int depth)
	{
		if(depth==cutOff)
		{
			return true;
		}
		return false;
	}
	public int MaxValue_alpha_beta(State s1,int alpha,int beta)
	{
		board_state b=s1.b;
		State s;
		int v=Integer.MIN_VALUE;
		if(myPlayer==1)
		{
			if(terminate_condition(s1))
			{
				if(s1.depth!=cutOff ||   (s1.depth==cutOff && s1.type.equals(s1.parent.type)))
				{
					try {
						traverse_log.write(s1.name+","+s1.depth+","+get_v(v)+","+get_v(alpha)+","+get_v(beta)+"\r\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}
				s1.value=s1.b.calculate_eval();
				try {
					traverse_log.write(s1.name+","+s1.depth+","+get_v(s1.value)+","+get_v(alpha)+","+get_v(beta)+"\r\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(s1.depth==1)
					temp_Tree.add(s1);
				return s1.value;
			}

			
			s1.value=v;
			try 
			{
				//System.out.println("MAX: "+s.name+","+s.depth+","+s.print()+"\r\n");
				traverse_log.write(s1.name+","+s1.depth+","+s1.print()+","+get_v(alpha)+","+get_v(beta)+"\r\n");
			} 
			catch (IOException e) 
			{
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for(int i=b.myStartPit(); i<=b.OppMancala+b.playerPits ;i++)
			{
				
				if(b.my_Pits[i].stones_count==0)
				{
					continue;
				}
				s=new State();
				board_state child= null;
				try 
				{
						child = (board_state)b.clone();		
						boolean flag=child.play1(i);
						s.b=child;
						s.name=b.my_Pits[i].name;
						System.out.println("Child:"+s.name+child);
						s.parent=s1;
						if(s1.type.equals("MAX"))
							s.depth=s1.depth+1;
						else
							s.depth=s1.depth;
						s.type="MIN";	
						if(flag)
						{	
							v=Math.max(v,MaxValue_alpha_beta(s,alpha,beta));
							s1.value=v;	
								
						}
						else
						{
							if(s.depth==cutOff)
							{
									s.value=s.b.calculate_eval();
									try {
										traverse_log.write(s.name+","+s.depth+","+get_v(s.value)+","+get_v(alpha)+","+get_v(beta)+"\r\n");
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									v=Math.max(v, s.value);
									s1.value=v;
									if(s.depth==1)
										temp_Tree.add(s);
									
									
							}
							else
							{
								v=Math.max(v,MinValue_alpha_beta(s,alpha,beta));
								s1.value=v;
							
							}
						}
						
						if(v>=beta)
						{
							
							try 
							{
							//System.out.println("MAX: "+s.name+","+s.depth+","+s.print()+"\r\n");
								traverse_log.write(s1.name+","+s1.depth+","+s1.print()+","+get_v(alpha)+","+get_v(beta)+"\r\n");
							} 
							catch (IOException e) 
							{
						// TODO Auto-generated catch block
							e.printStackTrace();
							}
	
							
							return v;
						}
						alpha=Math.max(alpha,v);
						
						
						try 
						{
			
							traverse_log.write(s1.name+","+s1.depth+","+s1.print()+","+get_v(alpha)+","+get_v(beta)+"\r\n");
						} 
						catch (IOException e) 
						{
					// TODO Auto-generated catch block
						e.printStackTrace();
						}
				} 
				catch (CloneNotSupportedException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(s1.depth==1)
				temp_Tree.add(s1);
			return v;

		}
		else
		{
			if(terminate_condition(s1))
			{
				if(s1.depth!=cutOff || (s1.depth==cutOff && s1.type.equals(s1.parent.type)))
				{
					try {
						traverse_log.write(s1.name+","+s1.depth+","+get_v(v)+","+get_v(alpha)+","+get_v(beta)+"\r\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}
				s1.value=s1.b.calculate_eval();
				try {
					traverse_log.write(s1.name+","+s1.depth+","+get_v(s1.value)+","+get_v(alpha)+","+get_v(beta)+"\r\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(s1.depth==1)
					temp_Tree.add(s1);
				return s1.value;
			}

			
			s1.value=v;
			try 
			{
				traverse_log.write(s1.name+","+s1.depth+","+s1.print()+","+get_v(alpha)+","+get_v(beta)+"\r\n");
			} 
			catch (IOException e) 
			{
		
				e.printStackTrace();
			}
			
			
			for(int i=(s1.b.size); i>s1.b.OppMancala;i--)
			{
				
				if(b.my_Pits[i].stones_count==0)
				{
					continue;
				}
				s=new State();
				board_state child= null;
				try 
				{
						child = (board_state)b.clone();
						
						boolean flag=child.play1(i);
						s.b=child;
						s.name=b.my_Pits[i].name;
						System.out.println("Child:"+s.name+child);
						s.parent=s1;
						if(s1.type.equals("MAX"))
							s.depth=s1.depth+1;
						else
							s.depth=s1.depth;
						s.type="MIN";	
						if(flag)
						{	
							v=Math.max(v,MaxValue_alpha_beta(s,alpha,beta));
							s1.value=v;	
						
						
						}
						else
						{
							if(s.depth==cutOff)
							{
									s.value=s.b.calculate_eval();
									try {
										traverse_log.write(s.name+","+s.depth+","+get_v(s.value)+","+get_v(alpha)+","+get_v(beta)+"\r\n");
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									v=Math.max(v, s.value);
									s1.value=v;
									if(s.depth==1)
										temp_Tree.add(s);
									
							}
							else
							{
								v=Math.max(v,MinValue_alpha_beta(s,alpha,beta));
								s1.value=v;
							}
							
							
						}	
						if(v>=beta)
						{
							
							try 
							{
						
								traverse_log.write(s1.name+","+s1.depth+","+s1.print()+","+get_v(alpha)+","+get_v(beta)+"\r\n");
							} 
							catch (IOException e) 
							{
						// TODO Auto-generated catch block
							e.printStackTrace();
							}
							
							return v;
						}
						alpha=Math.max(alpha,v);
						
						try 
						{
						
							traverse_log.write(s1.name+","+s1.depth+","+s1.print()+","+get_v(alpha)+","+get_v(beta)+"\r\n");
						} 
						catch (IOException e) 
						{
					// TODO Auto-generated catch block
						e.printStackTrace();
						}
				} 
				catch (CloneNotSupportedException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(s1.depth==1)
				temp_Tree.add(s1);
			return v;

		}
		
	}
	
	
	
	public int MinValue_alpha_beta(State s1,int alpha,int beta)
	{
		board_state b=s1.b;
		State s;
		int v=Integer.MAX_VALUE;
		if(myPlayer==1)
		{
			if(terminate_condition(s1))
			{
				if(s1.depth!=cutOff || (s1.depth==cutOff && s1.type.equals(s1.parent.type)))
				{
					try {
						traverse_log.write(s1.name+","+s1.depth+","+get_v(v)+","+get_v(alpha)+","+get_v(beta)+"\r\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}
			
				s1.value=s1.b.calculate_eval();
				try {
					traverse_log.write(s1.name+","+s1.depth+","+get_v(s1.value)+","+get_v(alpha)+","+get_v(beta)+"\r\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(s1.depth==1)
					temp_Tree.add(s1);
				return s1.value;
			}

		
			
			s1.value=v;
			try {
				traverse_log.write(s1.name+","+s1.depth+","+get_v(s1.value)+","+get_v(alpha)+","+get_v(beta)+"\r\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			for(int i=b.size;i>b.MyMancala ;i--)
			{
				if(b.my_Pits[i].stones_count==0)
				{
					continue;
				}
				s=new State();
				board_state child= null;
				try 
				{
						child = (board_state)b.clone();
						boolean flag=child.play2(i);
					
						s.b=child;
						s.name=b.my_Pits[i].name;
						s.parent=s1;
						if(s1.type.equals("MIN"))
							s.depth=s1.depth+1;
						else
							s.depth=s1.depth;
						s.type="MAX";
						
			
						if(flag)
						{	
							v=Math.min(v,MinValue_alpha_beta(s,alpha,beta));
							s1.value=v;
							
						}
						else
						{
							if(s.depth==cutOff)
							{
								s.value=s.b.calculate_eval();
								try {
									traverse_log.write(s.name+","+s.depth+","+get_v(s.value)+","+get_v(alpha)+","+get_v(beta)+"\r\n");
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								v=Math.min(v, s.value);	
								
								s1.value=v;
								if(s.depth==1)
									temp_Tree.add(s);						
							}
							else
							{	
								v=Math.min(v,MaxValue_alpha_beta(s,alpha,beta));
								s1.value=v;
							}	
						}	
						
						if(v<=alpha)
						{
							try {
								traverse_log.write(s1.name+","+s1.depth+","+get_v(s1.value)+","+get_v(alpha)+","+get_v(beta)+"\r\n");
							}
							catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							}
							return v;
						}
						
						beta=Math.min(beta, v);
						try {
							traverse_log.write(s1.name+","+s1.depth+","+get_v(s1.value)+","+get_v(alpha)+","+get_v(beta)+"\r\n");
						}
						catch (IOException e) {
						
						e.printStackTrace();
						}
						
				} 
				catch (CloneNotSupportedException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(s1.depth==1)
				temp_Tree.add(s1);
			return v;
	
		}
		else
		{
			if(terminate_condition(s1))
			{
				if(s1.depth!=cutOff || (s1.depth==cutOff && s1.type.equals(s1.parent.type)))
				{
					try {
						traverse_log.write(s1.name+","+s1.depth+","+get_v(v)+","+get_v(alpha)+","+get_v(beta)+"\r\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}
			
		
						
				
				s1.value=s1.b.calculate_eval();
				try {
					traverse_log.write(s1.name+","+s1.depth+","+get_v(s1.value)+","+get_v(alpha)+","+get_v(beta)+"\r\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(s1.depth==1)
					temp_Tree.add(s1);
				return s1.value;
			}

		
			
			s1.value=v;
			try {
				traverse_log.write(s1.name+","+s1.depth+","+get_v(s1.value)+","+get_v(alpha)+","+get_v(beta)+"\r\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(int i=b.otherStartPit();i<=b.MyMancala+b.playerPits ;i++)
			{
				if(b.my_Pits[i].stones_count==0)
				{
					continue;
				}
				s=new State();
				board_state child= null;
				try 
				{
						child = (board_state)b.clone();
						boolean flag=child.play2(i);
						s.b=child;
						s.name=b.my_Pits[i].name;
						s.parent=s1;
						if(s1.type.equals("MIN"))
							s.depth=s1.depth+1;
						else
							s.depth=s1.depth;
						s.type="MAX";
						
						if(flag)
						{	
							
							v=Math.min(v,MinValue_alpha_beta(s,alpha,beta));
							s1.value=v;
						}
						else
						{
							if(s.depth==cutOff)
							{
								s.value=s.b.calculate_eval();
								try {
									traverse_log.write(s.name+","+s.depth+","+get_v(s.value)+","+get_v(alpha)+","+get_v(beta)+"\r\n");
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								v=Math.min(v, s.value);	
								 
								s1.value=v;
								if(s.depth==1)
									temp_Tree.add(s);
																
							}
							else
							{	
								v=Math.min(v,MaxValue_alpha_beta(s,alpha,beta));
								s1.value=v;
								
							}
						}	
						
						if(v<=alpha)
						{
							try {
								traverse_log.write(s1.name+","+s1.depth+","+get_v(s1.value)+","+get_v(alpha)+","+get_v(beta)+"\r\n");
							}
							catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							}
							return v;
						}
						
						beta=Math.min(beta, v);
						try {
							traverse_log.write(s1.name+","+s1.depth+","+get_v(s1.value)+","+get_v(alpha)+","+get_v(beta)+"\r\n");
						}
						catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						}
						
						
				} 
				catch (CloneNotSupportedException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(s1.depth==1)
				temp_Tree.add(s1);
			return v;
	
		}
	}
	

	
	
	public String get_v(int val)
	{
		if(val==Integer.MAX_VALUE)
		{
			return "Infinity";
		}
		if(val==Integer.MIN_VALUE)
			return "-Infinity";
		return val+"";	
	}
}

class board_state implements Cloneable
{
	int OppMancala;
	int MyMancala;
	int size;
	int playerPits;
	int myplay;
	Pit_class my_Pits[];
	public board_state(board_state b)
	{
		this.OppMancala=b.OppMancala;
		this.MyMancala=b.MyMancala;
		this.my_Pits=b.my_Pits.clone();
		this.size=b.size;
		this.playerPits=b.playerPits;
		this.myplay=b.myplay;
	}
	
	public boolean allMyPitsempty()
	{
		if(myplay==1)
		{
			for(int i=2;i<=(playerPits+1);i++)
			{
				if(my_Pits[i].stones_count!=0)
					return false;
			}
			return true;
		}
		else
		if(myplay==2)
		{
			for(int i=(playerPits+3);i<=size;i++)
			{
				if(my_Pits[i].stones_count!=0)
					return false;
			}
			return true;
		}
		return false;
	}
	
	public boolean allOppPitsEmpty()
	{
		if(myplay==1)
		{
			for(int i=(playerPits+3);i<=size;i++)
			{
				if(my_Pits[i].stones_count!=0)
					return false;
			}
			return true;
		}
		else
		if(myplay==2)
		{
			for(int i=2;i<=(playerPits+1);i++)
			{ 
				if(my_Pits[i].stones_count!=0)
					return false;
			}
		}
		return true;
	}
	public boolean End_game_check()
	{
		boolean flag=false;
		if(myplay==1)
		{
			if(allMyPitsempty())
			{
				flag=true;
				for(int i=(playerPits+3);i<=size;i++)
				{
					if(my_Pits[i].stones_count!=0)
					{
						my_Pits[OppMancala].stones_count+=my_Pits[i].stones_count;
						my_Pits[i].stones_count=0;
					}
				}
			}
			else
			if(allOppPitsEmpty())
			{
				flag=true;
				for(int i=2;i<=(playerPits+1);i++)
				{
					if(my_Pits[i].stones_count!=0)
					{
						my_Pits[MyMancala].stones_count+=my_Pits[i].stones_count;
						my_Pits[i].stones_count=0;
					}
				}

			}
			return flag;

		}
		else
		if(myplay==2)
		{
			if(allMyPitsempty())
			{
				flag=true;
				for(int i=2;i<=playerPits+1;i++)
				{
					if(my_Pits[i].stones_count!=0)
					{
						my_Pits[OppMancala].stones_count+=my_Pits[i].stones_count;
						my_Pits[i].stones_count=0;
					}
				}
			}
			else
			if(allOppPitsEmpty())
			{
				flag=true;
				for(int i=playerPits+3;i<=size;i++)
				{
					if(my_Pits[i].stones_count!=0)
					{
						my_Pits[MyMancala].stones_count+=my_Pits[i].stones_count;
						my_Pits[i].stones_count=0;
					}
				}
			}
			return flag;
		}
		return flag;
	}
	
	
	public board_state( Pit_class[] Pits, int size,
			int playerPits,int player) {
		super();
		myplay=player;
		if(player==1)
		{
			MyMancala = (playerPits+2);
			OppMancala = 1;
		}
		else
		if(player==2)
		{
			OppMancala = (playerPits+2);
			MyMancala = 1;
		}
		this.my_Pits = Pits;
		this.size = size;
		this.playerPits = playerPits;
	}
	

	protected Object clone() throws CloneNotSupportedException {
		
		board_state b1 = null;
	    b1 = (board_state) super.clone();
	    b1.my_Pits=new Pit_class[size+1];
	    for(int i=1;i<=size;i++)
	    {
	    	b1.my_Pits[i]=(Pit_class) my_Pits[i].clone();
	    }
	    this.my_Pits.clone();
	    return b1;
	}
	
	public boolean isMyMancala(int num)
	{
		if(MyMancala==num)
			return true;
		return false;
	}
	
	public boolean Mancala2(int num)
	{
		if(OppMancala==num)
			return true;
		return false;
	}
	
	
	

	
	
	public int myStartPit()
	{
		if(myplay==1)
			return 2; 
		else
			return (OppMancala+1);
	}
	public int calculate_eval()
	{
			return my_Pits[MyMancala].stones_count-my_Pits[OppMancala].stones_count;
	}
	public boolean PlayerPit(int pitNumber)
	{
		if(myplay==1)
		{
			if(pitNumber>=2 && pitNumber<=playerPits+1)
				return true;
		}
		else
		if(myplay==2)
		{
			if(pitNumber>=playerPits+3 && pitNumber<=size)
				return true;
		}	
		return false;
	}
	@Override
	public String toString() {
		return Arrays.toString(my_Pits);
	}
	public boolean isEmpty(int pitNumber)
	{
		if(my_Pits[pitNumber].stones_count==1)
		{
			return true;
		}
		return false;
	}
	
	
	public boolean play1(int pit)
	{
		
		int stones=my_Pits[pit].stones_count;
		int pitNum=pit;
		my_Pits[pit].stones_count=0;
		while(stones!=0)
		{
			if((pitNum+1)>size)
			{
				pitNum=0;
			}
			if(Mancala2(pitNum+1))
				++pitNum;
			my_Pits[++pitNum].stones_count+=1;
			stones--;
		}
		
		if(isMyMancala(pitNum))
		{
			End_game_check();
			return true;
		}
		else
			if(End_game_check())
			{
				return false;
			}
			else
		if(PlayerPit(pitNum) && isEmpty(pitNum))
		{
		
			myEnd_game(pitNum);
			if(End_game_check())
			{
				return false;
			}
			return false;
		}
		if(End_game_check())
		{
			return false;
		}
		return false;
	}
	public int otherStartPit() {
		// TODO Auto-generated method stub
		if(myplay==1)
			return (MyMancala+1); 
		else
		
			return 2;
		
	}
	public boolean play2(int pit) {
	
		int stones=my_Pits[pit].stones_count;
		int pitNum=pit;
		my_Pits[pit].stones_count=0;
		while(stones!=0)
		{
			if((pitNum+1)>size)
			{
				pitNum=0;
			}
			
			if(isMyMancala(pitNum+1))
				++pitNum;
			my_Pits[++pitNum].stones_count+=1;
			stones--;
		}
		
		if(Mancala2(pitNum))
		{
			End_game_check();
			return true;
		}
			else
			if(End_game_check())
			{
				return false;
			}
			else
		if(!PlayerPit(pitNum) && !isMyMancala(pitNum) && isEmpty(pitNum))
		{
			Other_end_game(pitNum);
			if(End_game_check())
			{
				return false;
			}
			return false;
		}
		if(End_game_check())
		{
			return false;
		}
		return false;
	}
	
	private void Other_end_game(int pitNum) {
		String myPit=my_Pits[pitNum].name;
		String s=new String();
		if(myPit.charAt(0)=='A')
		{
			s=myPit.replace('A', 'B');
		}
		else
			s=myPit.replace('B', 'A');
		
		for(Pit_class p: my_Pits)
		{
		
			if(p!=null)
			{
		
				if(p.name.equals(s))
				{
		
					my_Pits[OppMancala].stones_count+=my_Pits[pitNum].stones_count;
					my_Pits[OppMancala].stones_count+=p.stones_count;
					p.stones_count=0;
					my_Pits[pitNum].stones_count=0;
				}
			}
		}
		
	}
	public void myEnd_game(int pitNum)
	{
		String myPit=my_Pits[pitNum].name;
		String s=new String();
		if(myPit.charAt(0)=='A')
		{
			s=myPit.replace('A', 'B');
		}
		else
			s=myPit.replace('B', 'A');
	
		for(Pit_class p: my_Pits)
		{
			if(p!=null)
			{
	
				if(p.name.equals(s))
				{
	
					my_Pits[MyMancala].stones_count+=my_Pits[pitNum].stones_count;
					my_Pits[MyMancala].stones_count+=p.stones_count;
					p.stones_count=0;
					my_Pits[pitNum].stones_count=0;
					End_game_check();
					return;
				}
			}
		}	
	}
}


class  Pit_class implements Cloneable
{
	int pit;
	 String name;
	 int stones_count;
	
	
	public String toString() {
		return name+"-"+stones_count+" ";
	}
	
	
	
	public Pit_class() {
		super();
		
	}
	public Pit_class(String name, int stones) {
		super();
		this.name = name;
		this.stones_count = stones;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		
		Pit_class p=(Pit_class) super.clone();
		p.name=name;
		p.stones_count=stones_count;
		return p;
		
	}
}

