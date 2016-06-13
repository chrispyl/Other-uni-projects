package facilityGame;

import java.util.Vector;

public class Pancake extends FPlayer {
	static String playerName = "Pancake";
	static String version = "1.0";
	// Give your personal information in Greek
	static int afm = 56184; // AFM should be in form 5XXXX
	static String firstname = "Χρήστος";
	static String lastname = "Πυλιανίδης";
	static int curMoveIndex;

	
	public Pancake(EnumPlayer player) {
		super(player, playerName, version, afm, firstname, lastname);
	}

	// Initialize Player
	// This method is called before the game starts
	// Each player can override this method
	public void initialize(FacilityGameAPI game) {
		
	}
	
	
	
	public int acquireMaxPositions(Vector<Integer> themaxpositions, FacilityGameAPI thegame)   //find overall max
	{
		int n=thegame.getN();
		int max=0;
		
		for(int i=0; i<n; i++)
		{
			if(thegame.getStatus(i) == EnumFacilityStatus.FREE)
			{
				if(thegame.getValue(i)>=max)
				{
					max=thegame.getValue(i);
				}
			}
		}
		
		for(int i=0; i<n; i++)
		{
			if(thegame.getStatus(i) == EnumFacilityStatus.FREE)
			{
				if(thegame.getValue(i)>=max)
				{
					themaxpositions.add(i);
				}
			}
		}
		
		return max;
	}
	
	public int calculateBoundaries(int currentmax, int n)
	{
		
		int boundary;
		
		if(currentmax>=45)
		{
			boundary=45;
		}
		else if(currentmax>=40)
		{
			boundary=40;
		}
		else if(currentmax>=35)
		{
			boundary=35;
		}
		else if(currentmax>=30)
		{
			boundary=30;
		}
		else if(currentmax>=25)
		{
			boundary=25;
		}
		else if(currentmax>=20)
		{
			boundary=20;
		}
		else if(currentmax>=15)
		{
			boundary=15;
		}
		else if(currentmax>=10)
		{
			boundary=10;
		}
		else if(currentmax>=5)
		{
			boundary=5;
		}
		else
		{
			boundary=0;
		}
		
		if(n>150)
		{
			if( curMoveIndex<60) boundary=boundary+3;
		}
		
		if(n<150) boundary=boundary-5;
		return boundary;
	}
	
	public int howManyMaxes(int currentmax, FacilityGameAPI thegame)
	{
		int n=thegame.getN();
		int numberofmaxes=0;
		int boundary=calculateBoundaries(currentmax, n);
		
		for(int i=0; i<n; i++)
		{
			if(thegame.getStatus(i) == EnumFacilityStatus.FREE)
			{
				if(thegame.getValue(i)>=boundary)
				{
					numberofmaxes++;
				}
			}
		}
		
		return numberofmaxes;
	}
	
	public boolean belongsToMe(int i, FacilityGameAPI thegame)
	{
		if(thegame.getStatus(i) == EnumFacilityStatus.PLAYER_A && whoAmI()==EnumPlayer.PLAYER_A)
		{
			return true;
		}
		else if(thegame.getStatus(i) == EnumFacilityStatus.PLAYER_B && whoAmI()==EnumPlayer.PLAYER_B)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean belongsToOther(int i, FacilityGameAPI thegame)
	{
		if(thegame.getStatus(i) == EnumFacilityStatus.PLAYER_A && whoIsMyOpponent()==EnumPlayer.PLAYER_A)
		{
			return true;
		}
		else if(thegame.getStatus(i) == EnumFacilityStatus.PLAYER_B && whoIsMyOpponent()==EnumPlayer.PLAYER_B)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean exists(int i, int n)
	{
		if(i>=0 && i<=n-1)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public int bestOfFour(int previous, int moreprevious, int next, int morenext, FacilityGameAPI thegame) //takes indexes not node values
	{
		int n = thegame.getN();
		int max=-10;					
		int maxpos=-10;					
		int [] matrix=new int[4];
		matrix[0]=previous;
		matrix[1]=moreprevious;
		matrix[2]=next;
		matrix[3]=morenext;
		
		
		for(int i=0; i<4; i++)
		{
			if(exists(matrix[i],n)==false || thegame.getStatus(matrix[i])!=EnumFacilityStatus.FREE) matrix[i]=-1;
		}
		
		for(int i=0; i<4; i++)
		{
			if(matrix[i]!=-1)
			{
				if(thegame.getValue(matrix[i])>max) 
				{
					max=thegame.getValue(matrix[i]);
					maxpos=i;
				}
				
			}
		}
		
		if(max!=-10)
		{
			return matrix[maxpos];
		}
		else
		{
			return -1;
		}
	}
	
	public int higherOfThree(int first, int second, int third, FacilityGameAPI thegame)
	{
		int max=-1;
		int maxpos=-1;
		int [] nums = new int[3];
		nums[0]=first;
		nums[1]=second;
		nums[2]=third;
		
		for(int i=0;i<3;i++)
		{
			if(thegame.getValue(nums[i])>max)
			{
				max=thegame.getValue(nums[i]);
				maxpos=i;
			}
		}
		
		if(thegame.getValue(nums[0])==thegame.getValue(nums[1]) && thegame.getValue(nums[1])==thegame.getValue(nums[2]))
		{
			return nums[1];
		}
		else
		{
			return nums[maxpos];
		}
	}
	
	public void findContinuous(Vector<Integer> profitofothermultbestchoice, Vector<Integer> profitofmymultbestchoice, Vector<Integer> mymultiples, Vector<Integer> othermultiples, Vector<Integer> sumofmymultiples, Vector<Integer> sumofothermultiples, Vector<Integer> mymultbestchoice, Vector<Integer> othermultbestchoice, Vector<Integer> myduals, Vector<Integer> otherduals, Vector<Integer> profitofmydualswithbestchoice, Vector<Integer> profitofotherdualswithbestchoice, Vector<Integer> mydualsbestchoice, Vector<Integer> otherdualsbestchoice, FacilityGameAPI thegame)
	{
		
		int n = thegame.getN();
		int summysequence=0, sumothersequence=0;
		int freeflag=0, myflag=0, otherflag=0;
		int freesequence=0, mysequence=0, othersequence=0;
		int startofsequence=-1;
		
		for(int i=0;i<n;i++)     //Start putting flags to detect continuities
		{
			if(((thegame.getStatus(i) == EnumFacilityStatus.FREE || thegame.getStatus(i) == EnumFacilityStatus.BLOCKED) && freeflag==1))
			{
				if(thegame.getStatus(i) == EnumFacilityStatus.FREE) freesequence++;
			}
			else if(((belongsToMe(i, thegame) || thegame.getStatus(i) == EnumFacilityStatus.BLOCKED) && myflag==1))
			{
				if(belongsToMe(i, thegame)) 
				{
					mysequence++;
					summysequence=summysequence+thegame.getValue(i);
				}
			}
			else if(((belongsToOther(i, thegame) || thegame.getStatus(i) == EnumFacilityStatus.BLOCKED) && otherflag==1))
			{
				if(belongsToOther(i, thegame)) 
				{
					othersequence++;
					sumothersequence=sumothersequence+thegame.getValue(i);
				}
			}
			else
			{
				if(thegame.getStatus(i) == EnumFacilityStatus.FREE)
				{
					startofsequence=i;
					freeflag=1;
					myflag=0;
					otherflag=0;
					freesequence=1;
					mysequence=0;
					othersequence=0;
				}
				else if(belongsToMe(i, thegame))
				{
					startofsequence=i;
					freeflag=0;
					myflag=1;
					otherflag=0;
					freesequence=0;
					mysequence=1;
					othersequence=0;
					summysequence=thegame.getValue(i);
				}
				else if(belongsToOther(i, thegame))
				{
					startofsequence=i;
					freeflag=0;
					myflag=0;
					otherflag=1;
					freesequence=0;
					mysequence=0;
					othersequence=1;
					sumothersequence=thegame.getValue(i);
				}
			}
			
			//if these are true the sequence is over 	//check only for i+1 because 'for' goes from left to right      
			if(mysequence>=3)
			{
				if(exists(i+1, n))
				{
					if(exists(i+2, n))
					{
						if(exists(i+3, n))
						{
							if(belongsToMe(i+1, thegame)==false && belongsToMe(i+2, thegame)==false && belongsToMe(i+3, thegame)==false)
							{
								mymultiples.add(startofsequence);
								mymultbestchoice.add(bestOfFour(startofsequence-2, startofsequence-3, i+1, i+2, thegame));
								if(mymultbestchoice.lastElement()==-1)
								{
									profitofmymultbestchoice.add(-1);
								}
								else
								{
									profitofmymultbestchoice.add(3*thegame.getValue(mymultbestchoice.lastElement()));
								}
								sumofmymultiples.add(summysequence);
							}
						}
						else
						{
							if(belongsToMe(i+1, thegame)==false && belongsToMe(i+2, thegame)==false)
							{
								mymultiples.add(startofsequence);
								mymultbestchoice.add(bestOfFour(startofsequence-2, startofsequence-3, i+1, i+2, thegame));
								if(mymultbestchoice.lastElement()==-1)
								{
									profitofmymultbestchoice.add(-1);
								}
								else
								{
									profitofmymultbestchoice.add(3*thegame.getValue(mymultbestchoice.lastElement()));
								}
								sumofmymultiples.add(summysequence);
							}
						}
					}
					else
					{
						if(belongsToMe(i+1, thegame)==false)
						{
							mymultiples.add(startofsequence);
							mymultbestchoice.add(bestOfFour(startofsequence-2, startofsequence-3, i+1, i+2, thegame));
							if(mymultbestchoice.lastElement()==-1)
							{
								profitofmymultbestchoice.add(-1);
							}
							else
							{
								profitofmymultbestchoice.add(3*thegame.getValue(mymultbestchoice.lastElement()));
							}
							sumofmymultiples.add(summysequence);
						}
					}
				}
				else
				{
					mymultiples.add(startofsequence);
					mymultbestchoice.add(bestOfFour(startofsequence-2, startofsequence-3, i+1, i+2, thegame));
					if(mymultbestchoice.lastElement()==-1)
					{
						profitofmymultbestchoice.add(-1);
					}
					else
					{
						profitofmymultbestchoice.add(3*thegame.getValue(mymultbestchoice.lastElement()));
					}
					sumofmymultiples.add(summysequence);
				}
			}
			if(mysequence==2)
			{
				if(exists(i+1, n))
				{
					if(exists(i+2, n))
					{
						if(exists(i+3, n))
						{
							if(belongsToMe(i+1, thegame)==false && belongsToMe(i+2, thegame)==false && belongsToMe(i+3, thegame)==false)
							{
								myduals.add(startofsequence);
								mydualsbestchoice.add(bestOfFour(startofsequence-2, startofsequence-3, i+1, i+2, thegame));
								if(mydualsbestchoice.lastElement()==-1)
								{
									profitofmydualswithbestchoice.add(-1);
								}
								else
								{
									profitofmydualswithbestchoice.add(2*summysequence+3*thegame.getValue(mydualsbestchoice.lastElement()));
								}
							}
						}
						else
						{
							if(belongsToMe(i+1, thegame)==false && belongsToMe(i+2, thegame)==false)
							{
								myduals.add(startofsequence);
								mydualsbestchoice.add(bestOfFour(startofsequence-2, startofsequence-3, i+1, i+2, thegame));
								if(mydualsbestchoice.lastElement()==-1)
								{
									profitofmydualswithbestchoice.add(-1);
								}
								else
								{
									profitofmydualswithbestchoice.add(2*summysequence+3*thegame.getValue(mydualsbestchoice.lastElement()));
								}
							}
						}
					}
					else
					{
						if(belongsToMe(i+1, thegame)==false)
						{
							myduals.add(startofsequence);
							mydualsbestchoice.add(bestOfFour(startofsequence-2, startofsequence-3, i+1, i+2, thegame));
							if(mydualsbestchoice.lastElement()==-1)
							{
								profitofmydualswithbestchoice.add(-1);
							}
							else
							{
								profitofmydualswithbestchoice.add(2*summysequence+3*thegame.getValue(mydualsbestchoice.lastElement()));
							}
						}
					}
				}
				else
				{
					myduals.add(startofsequence);
					mydualsbestchoice.add(bestOfFour(startofsequence-2, startofsequence-3, i+1, i+2, thegame));
					if(mydualsbestchoice.lastElement()==-1)
					{
						profitofmydualswithbestchoice.add(-1);
					}
					else
					{
						profitofmydualswithbestchoice.add(2*summysequence+3*thegame.getValue(mydualsbestchoice.lastElement()));
					}
				}
			}			
			
			
			if(othersequence>=3)
			{
				if(exists(i+1, n))
				{
					if(exists(i+2, n))
					{
						if(exists(i+3, n))
						{
							if(belongsToOther(i+1, thegame)==false && belongsToOther(i+2, thegame)==false && belongsToOther(i+3, thegame)==false)
							{
								othermultiples.add(startofsequence);
								othermultbestchoice.add(bestOfFour(startofsequence-2, startofsequence-3, i+1, i+2, thegame));
								if(othermultbestchoice.lastElement()==-1)
								{
									profitofothermultbestchoice.add(-1);
								}
								else
								{
									profitofothermultbestchoice.add(3*thegame.getValue(othermultbestchoice.lastElement()));
								}
								sumofothermultiples.add(sumothersequence);
							}
						}
						else
						{
							if(belongsToOther(i+1, thegame)==false && belongsToOther(i+2, thegame)==false)
							{
								othermultiples.add(startofsequence);
								othermultbestchoice.add(bestOfFour(startofsequence-2, startofsequence-3, i+1, i+2, thegame));
								if(othermultbestchoice.lastElement()==-1)
								{
									profitofothermultbestchoice.add(-1);
								}
								else
								{
									profitofothermultbestchoice.add(3*thegame.getValue(othermultbestchoice.lastElement()));
								}
								sumofothermultiples.add(sumothersequence);
							}
						}
					}
					else
					{
						if(belongsToOther(i+1, thegame)==false)
						{
							othermultiples.add(startofsequence);
							othermultbestchoice.add(bestOfFour(startofsequence-2, startofsequence-3, i+1, i+2, thegame));
							if(othermultbestchoice.lastElement()==-1)
							{
								profitofothermultbestchoice.add(-1);
							}
							else
							{
								profitofothermultbestchoice.add(3*thegame.getValue(othermultbestchoice.lastElement()));
							}
							sumofothermultiples.add(sumothersequence);
						}
					}
				}
				else
				{
					othermultiples.add(startofsequence);
					othermultbestchoice.add(bestOfFour(startofsequence-2, startofsequence-3, i+1, i+2, thegame));
					if(othermultbestchoice.lastElement()==-1)
					{
						profitofothermultbestchoice.add(-1);
					}
					else
					{
						profitofothermultbestchoice.add(3*thegame.getValue(othermultbestchoice.lastElement()));
					}
					sumofothermultiples.add(sumothersequence);
				}
			}
			if(othersequence==2)
			{
				if(exists(i+1, n))
				{
					if(exists(i+2, n))
					{
						if(exists(i+3, n))
						{
							if(belongsToOther(i+1, thegame)==false && belongsToOther(i+2, thegame)==false && belongsToOther(i+3, thegame)==false)
							{
								otherduals.add(startofsequence);
								otherdualsbestchoice.add(bestOfFour(startofsequence-2, startofsequence-3, i+1, i+2, thegame));
								if(otherdualsbestchoice.lastElement()==-1)
								{
									profitofotherdualswithbestchoice.add(-1);
								}
								else
								{
									profitofotherdualswithbestchoice.add(2*sumothersequence+3*thegame.getValue(otherdualsbestchoice.lastElement()));
								}
							}
						}
						else
						{
							if(belongsToOther(i+1, thegame)==false && belongsToOther(i+2, thegame)==false)
							{
								otherduals.add(startofsequence);
								otherdualsbestchoice.add(bestOfFour(startofsequence-2, startofsequence-3, i+1, i+2, thegame));
								if(otherdualsbestchoice.lastElement()==-1)
								{
									profitofotherdualswithbestchoice.add(-1);
								}
								else
								{
									profitofotherdualswithbestchoice.add(2*sumothersequence+3*thegame.getValue(otherdualsbestchoice.lastElement()));
								}
							}
						}
					}
					else
					{
						if(belongsToOther(i+1, thegame)==false)
						{
							otherduals.add(startofsequence);
							otherdualsbestchoice.add(bestOfFour(startofsequence-2, startofsequence-3, i+1, i+2, thegame));
							if(otherdualsbestchoice.lastElement()==-1)
							{
								profitofotherdualswithbestchoice.add(-1);
							}
							else
							{
								profitofotherdualswithbestchoice.add(2*sumothersequence+3*thegame.getValue(otherdualsbestchoice.lastElement()));
							}
						}
					}
				}
				else
				{
					otherduals.add(startofsequence);
					otherdualsbestchoice.add(bestOfFour(startofsequence-2, startofsequence-3, i+1, i+2, thegame));
					if(otherdualsbestchoice.lastElement()==-1)
					{
						profitofotherdualswithbestchoice.add(-1);
					}
					else
					{
						profitofotherdualswithbestchoice.add(2*sumothersequence+3*thegame.getValue(otherdualsbestchoice.lastElement()));
					}
				}
			}
				
		}
	}
	
	public void findNeighborhoods(Vector<Integer> thegeitonies, Vector<Integer> theendofgeitonies, Vector<Integer> theposageitniazoun, Vector<Integer> thegeitoniessum, Vector<Integer> theexeimonossegeitonia, Vector<Integer> theexwmonossegeitonia, int thecurrentmax, FacilityGameAPI thegame) 			
	{
		
		int n = thegame.getN();
		int flagfound=0;
		int firstfound=0;
		int counter=3;
		int continuous=1;
		int start = 0;
		int wasthepreviousmax=0;
		int previousmaxpos=0;           //had to put initial values
		int sumforqual=0;
		int lastgeit=-1;
		int iamthere=0;
		int icameherefirst=0;
		int heisthere=0;
		int hecameherefirst=0;
		int hasatleast1free=0;
		int boundary=calculateBoundaries(thecurrentmax, n);
		
		
		for(int i=0; i<n; i++)							//check for neighborhoods
		{
			if(counter==0 || (iamthere==1 && heisthere==1)) 
			{
				if(continuous>=2 && hasatleast1free==1) 
				{
					if(hecameherefirst==1) theexeimonossegeitonia.add(start);
					if(icameherefirst==1) theexwmonossegeitonia.add(start);
					thegeitonies.add(start);
					theendofgeitonies.add(lastgeit);
					theposageitniazoun.add(continuous);
					thegeitoniessum.add(sumforqual);
				}
				flagfound=0;
				firstfound=0;
				counter=3;     //3 positions can be connected to form continuities. 2 in the middle kai 1 for the next
				continuous=1;
				sumforqual=0;
				if(iamthere==1 && heisthere==1) i--;
				iamthere=0;
				icameherefirst=0;
				heisthere=0;
				hecameherefirst=0;
				hasatleast1free=0;
			}
			if(flagfound==1 && i-previousmaxpos==1)
			{
				wasthepreviousmax=1;
			}
			else
			{
				wasthepreviousmax=0;
			}
			if(thegame.getValue(i)>=boundary && wasthepreviousmax==0 && thegame.getStatus(i)!=EnumFacilityStatus.BLOCKED)
			{
				if(exists(i+1, n))
				{
					if(thegame.getValue(i+1)>thegame.getValue(i) && counter==2 && flagfound==1 && thegame.getStatus(i+1)!=EnumFacilityStatus.BLOCKED)
					{
						i++;
					}
				}
				if(thegame.getStatus(i)==EnumFacilityStatus.FREE) hasatleast1free=1;
				if(flagfound==0) start=i;
				if(belongsToMe(i, thegame))
				{
					iamthere=1;
					if(heisthere==0) icameherefirst=1;
				}
				if(belongsToOther(i, thegame))
				{
					heisthere=1;
					if(iamthere==0) hecameherefirst=1;
				}
				if(!(iamthere==1 && heisthere==1))
				{
					flagfound=1;
					counter=3;
					previousmaxpos=i;
					wasthepreviousmax=1;
					lastgeit=i;
					sumforqual=sumforqual+thegame.getValue(i);
					if(firstfound==1) continuous++;
				}
			}
			if(flagfound==1)
			{
				if(firstfound==1)
				{
					counter--;
				}
				else
				{
					firstfound=1;
				}
			}
			if(i==n-1)
			{
				if(continuous>=2 && hasatleast1free==1)
				{
					if(hecameherefirst==1) theexeimonossegeitonia.add(start);
					if(icameherefirst==1) theexwmonossegeitonia.add(start);
					thegeitonies.add(start);
					theendofgeitonies.add(lastgeit);
					theposageitniazoun.add(continuous);
					thegeitoniessum.add(sumforqual);
				}
			}
		}
	}
	
	public int globalPositionOfNeighborhoodElement(int currentmax, int neighborhoodchosen, int stoixeioofneighborhoodchosen, Vector<Integer> geitonies, FacilityGameAPI thegame)
	{
		
		int n=thegame.getN();
		int howmanyplacesafter = geitonies.elementAt(neighborhoodchosen);
		int previous=0;
		int which=-1;
		int boundary=calculateBoundaries(currentmax, n);
		
		while(true)
		{
			if(previous==0)
			{
				if(thegame.getValue(howmanyplacesafter)>=boundary) 
				{
					if(exists(howmanyplacesafter+1, n))
					{
						if(thegame.getValue(howmanyplacesafter+1)>thegame.getValue(howmanyplacesafter) && howmanyplacesafter!=geitonies.elementAt(neighborhoodchosen))
						{
							which++;
							howmanyplacesafter++;
						}
						else
						{
							which++;
							previous=1;
						}
					}
					else
					{
						which++;
						previous=1;
					}
				}
			}
			if(which==stoixeioofneighborhoodchosen) break;
			if(previous==1)
			{
				howmanyplacesafter=howmanyplacesafter+2;
				previous=0;
			}
			else
			{
				howmanyplacesafter++;
			}
		}
		
		return howmanyplacesafter;
	}
	
	public int findMaxNeighborhood(Vector<Integer> theposageitniazoun)
	{
		int max=-1;
		for(int i=0; i<theposageitniazoun.size(); i++)
		{
			if(theposageitniazoun.elementAt(i)>max)
			{
				max=theposageitniazoun.elementAt(i);
			}
		}
		
		return max;
	}
	
	public int findHowManyMaxNeighborhoods(Vector<Integer> theposageitniazoun, int maxneighborhood, Vector<Integer> thekanonik, Vector<Integer> themaxgeitonies, Vector<Integer> thegeitonies)
	{
		int sum=0;
		
		for(int i=0; i<theposageitniazoun.size(); i++)
		{
			if(theposageitniazoun.elementAt(i)==maxneighborhood)
			{
				themaxgeitonies.add(thegeitonies.elementAt(i));
				thekanonik.add(i);
				sum++;
			}
		}
		
		return sum;
	}
	
	public void findMyKotsarismata(Vector<Integer> theimprovedmykotsarisma, Vector<Integer> thekerdosofmykotsarisma, FacilityGameAPI thegame)
	{
		
		int n = thegame.getN();
		boolean mine=false;
		boolean mine2=false;
		int sunex=0;     //first phase continuities
		int sunex2=0;   //third phase continuities
		int frees=0;    //free (second phase) 
		boolean freeflag=false;
		int sunexsum=0;
		int freesum=0;
		int sunex2sum=0;
		int kerdos=0;
		int startoffrees=-1;
		int startofmine2=-1;

		for(int i=0; i<n; i++)
		{
			if(belongsToOther(i, thegame) && mine2==false)
			{
				mine=false;
				mine2=false;
				freeflag=false;
				sunex=0;
				sunex2=0;
				frees=0;
				sunexsum=0;
				sunex2sum=0;
				kerdos=0;
			}
			if(belongsToMe(i, thegame) && freeflag==false && mine2==false)
			{
				mine=true;
				sunex++;
				sunexsum+=thegame.getValue(i);
			}
			if(sunex>0 && thegame.getStatus(i)==EnumFacilityStatus.FREE && mine2==false)
			{
				if(freeflag==false) startoffrees=i;
				mine=false;
				freeflag=true;
				frees++;
			}
			if(mine==false && freeflag==true && belongsToMe(i, thegame))
			{
				if(mine2==false) startofmine2=i;
				mine2=true;
				sunex2++;
				sunex2sum+=thegame.getValue(i);
			}
			if(mine2==true && (belongsToOther(i, thegame) || thegame.getStatus(i)==EnumFacilityStatus.FREE))
			{

				if(frees>0 && frees<=3)
				{
					if(sunex<3)
					{
						kerdos=sunexsum*2;
					}
					if(sunex2<3)
					{
						kerdos=kerdos+sunex2sum*2;
					}
					if(frees==1)
					{
						kerdos=kerdos+freesum*3;
						theimprovedmykotsarisma.add(startoffrees);
					}
					else if(frees==2)
					{
						if(thegame.getValue(startoffrees)>thegame.getValue(startoffrees+1))
						{
							kerdos=kerdos+3*thegame.getValue(startoffrees);
							theimprovedmykotsarisma.add(startoffrees);
						}
						else
						{
							kerdos=kerdos+3*thegame.getValue(startoffrees+1);
							theimprovedmykotsarisma.add(startoffrees+1);
						}
					}
					else
					{
						kerdos=kerdos+3*thegame.getValue(startoffrees+1);
						theimprovedmykotsarisma.add(startoffrees+1);
					}
					thekerdosofmykotsarisma.add(kerdos);
				}
				if(mine2==true && thegame.getStatus(i)==EnumFacilityStatus.FREE)
				{
				i=i-(i-startofmine2)-1;
				}
				mine=false;
				mine2=false;
				freeflag=false;
				sunex=0;
				sunex2=0;
				frees=0;
				sunexsum=0;
				sunex2sum=0;
				kerdos=0;
			}		
			if(i==n-1)
			{
				if(mine2==true)
				{
					if(frees>0 && frees<=3)
					{
						if(sunex<3)
						{
							kerdos=sunexsum*2;
						}
						if(sunex2<3)
						{
							kerdos=kerdos+sunex2sum*2;
						}
						if(frees==1)
						{
							kerdos=kerdos+freesum*3;
							theimprovedmykotsarisma.add(startoffrees);
						}
						else if(frees==2)
						{
							if(thegame.getValue(startoffrees)>thegame.getValue(startoffrees+1))
							{
								kerdos=kerdos+3*thegame.getValue(startoffrees);
								theimprovedmykotsarisma.add(startoffrees);
							}
							else
							{
								kerdos=kerdos+3*thegame.getValue(startoffrees+1);
								theimprovedmykotsarisma.add(startoffrees+1);
							}
						}
						else
						{
							kerdos=kerdos+3*thegame.getValue(startoffrees+1);
							theimprovedmykotsarisma.add(startoffrees+1);
						}
						thekerdosofmykotsarisma.add(kerdos);
					}
					
				}
			}
		}
		
	}
	
	public void findOtherKotsarismata(Vector<Integer> theimprovedotherkotsarisma, Vector<Integer> thekerdosofotherkotsarisma, FacilityGameAPI thegame)
	{
		
		int n = thegame.getN();
		boolean his=false;
		boolean his2=false;
		int sunex=0;     //first phase continuities
		int sunex2=0;   //third phase continuities
		int frees=0;    //free (second phase)
		boolean freeflag=false;
		int sunexsum=0;
		int freesum=0;
		int sunex2sum=0;
		int kerdos=0;
		int startoffrees=-1;
		int startofhis2=-1;

		for(int i=0; i<n; i++)
		{
			if(belongsToMe(i, thegame) && his2==false)
			{
				his=false;
				his2=false;
				freeflag=false;
				sunex=0;
				sunex2=0;
				frees=0;
				sunexsum=0;
				sunex2sum=0;
				kerdos=0;
			}
			if(belongsToOther(i, thegame) && freeflag==false && his2==false)
			{
				his=true;
				sunex++;
				sunexsum+=thegame.getValue(i);
			}
			if(sunex>0 && thegame.getStatus(i)==EnumFacilityStatus.FREE && his2==false)
			{
				if(freeflag==false) startoffrees=i;
				his=false;
				freeflag=true;
				frees++;
			}
			if(his==false && freeflag==true && belongsToOther(i, thegame))
			{
				if(his2==false) startofhis2=i;
				his2=true;
				sunex2++;
				sunex2sum+=thegame.getValue(i);
			}
			if(his2==true && (belongsToMe(i, thegame) || thegame.getStatus(i)==EnumFacilityStatus.FREE))
			{

				if(frees>0 && frees<=3)
				{
					if(sunex<3)
					{
						kerdos=sunexsum*2;
					}
					if(sunex2<3)
					{
						kerdos=kerdos+sunex2sum*2;
					}
					if(frees==1)
					{
						kerdos=kerdos+freesum*3;
						theimprovedotherkotsarisma.add(startoffrees);
					}
					else if(frees==2)
					{
						if(thegame.getValue(startoffrees)>thegame.getValue(startoffrees+1))
						{
							kerdos=kerdos+3*thegame.getValue(startoffrees);
							theimprovedotherkotsarisma.add(startoffrees);
						}
						else
						{
							kerdos=kerdos+3*thegame.getValue(startoffrees+1);
							theimprovedotherkotsarisma.add(startoffrees+1);
						}
					}
					else
					{
						kerdos=kerdos+3*thegame.getValue(startoffrees+1);
						theimprovedotherkotsarisma.add(startoffrees+1);
					}
					thekerdosofotherkotsarisma.add(kerdos);
				}
				if(his2==true && thegame.getStatus(i)==EnumFacilityStatus.FREE)
				{
				i=i-(i-startofhis2)-1;
				}
				his=false;
				his2=false;
				freeflag=false;
				sunex=0;
				sunex2=0;
				frees=0;
				sunexsum=0;
				sunex2sum=0;
				kerdos=0;
			}		
			if(i==n-1)
			{
				if(his2==true)
				{
					if(frees>0 && frees<=3)
					{
						if(sunex<3)
						{
							kerdos=sunexsum*2;
						}
						if(sunex2<3)
						{
							kerdos=kerdos+sunex2sum*2;
						}
						if(frees==1)
						{
							kerdos=kerdos+freesum*3;
							theimprovedotherkotsarisma.add(startoffrees);
						}
						else if(frees==2)
						{
							if(thegame.getValue(startoffrees)>thegame.getValue(startoffrees+1))
							{
								kerdos=kerdos+3*thegame.getValue(startoffrees);
								theimprovedotherkotsarisma.add(startoffrees);
							}
							else
							{
								kerdos=kerdos+3*thegame.getValue(startoffrees+1);
								theimprovedotherkotsarisma.add(startoffrees+1);
							}
						}
						else
						{
							kerdos=kerdos+3*thegame.getValue(startoffrees+1);
							theimprovedotherkotsarisma.add(startoffrees+1);
						}
						thekerdosofotherkotsarisma.add(kerdos);
					}
					
				}
			}
		}
		
	}
	
	public void myMoveForMaxpoints(Vector<Integer> higherpointshecanget, Vector<Integer> improvedmykotsarisma, Vector<Integer> mymultbestchoice, Vector<Integer> mydualsbestchoice, Vector<Integer> kerdosofmykotsarisma, Vector<Integer> profitofmymultbestchoice, Vector<Integer> profitofmydualswithbestchoice, Vector<Integer> higherpointsicanget, Vector<Integer> positionofhigherpointsicanget, Vector<Integer> maxpositions, FacilityGameAPI thegame)
	{
		int[] tobecompared=new int[4];
		int n = thegame.getN();
		int maxkerdh=-1;
		int maxkerdpos=-1;
		int maxmult=-1;
		int maxmultpos=-1;
		int maxdual=-1;
		int maxdualpos=-1;
		int maxkoino=-1;
		int maxkoinopos=-1;
		int theking=-1;
		int thekingpos=-1;
		int temp;
		
		for(int i=0; i<kerdosofmykotsarisma.size(); i++)
		{
			if(kerdosofmykotsarisma.elementAt(i)>maxkerdh)
			{
				maxkerdh=kerdosofmykotsarisma.elementAt(i);
				maxkerdpos=i;
			}
		}
		tobecompared[0]=maxkerdh;
		
		for(int i=0; i<profitofmymultbestchoice.size(); i++)
		{
			if(profitofmymultbestchoice.elementAt(i)>maxmult)
			{
				maxmult=profitofmymultbestchoice.elementAt(i);
				maxmultpos=i;
			}
		}		
		tobecompared[1]=maxmult;		
		
		for(int i=0; i<profitofmydualswithbestchoice.size(); i++)
		{
			if(profitofmydualswithbestchoice.elementAt(i)>maxdual)
			{
				maxdual=profitofmydualswithbestchoice.elementAt(i);
				maxdualpos=i;
			}
		}	
		tobecompared[2]=maxdual;
		
		for(int i=0; i<n; i++)
		{
			if(thegame.getStatus(i)==EnumFacilityStatus.FREE)
			{
				if(thegame.getValue(i)>maxkoino)                  
				{
					maxkoino=thegame.getValue(i);       
					maxkoinopos=i;
				}
			}
		}
		tobecompared[3]=maxkoino;

		for(int i=0; i<4; i++)
		{
			if(tobecompared[i]!=-1)
			{
				if(tobecompared[i]>theking)
				{
					theking=tobecompared[i];
					thekingpos=i;
				}
			}
		}
	
		if(n>150)
		{
			if(thekingpos==0)
			{
				if(maxkerdh<4*maxkoino) //(0.8*5)
				{
					thekingpos=3;
				}
			}
			else if(thekingpos==1)
			{
				if(maxmult<2*maxkoino)
				{
					thekingpos=3;
				}
			}
			else if(thekingpos==2)
			{
				if(maxdual<5.6*maxkoino) //(0.8*7)
				{
					thekingpos=3;
				}
			}
		}

		if(thekingpos==0)
		{
			positionofhigherpointsicanget.add(improvedmykotsarisma.elementAt(maxkerdpos));
			higherpointsicanget.add(maxkerdh);
		}
		else if(thekingpos==1)
		{
			positionofhigherpointsicanget.add(mymultbestchoice.elementAt(maxmultpos));
			higherpointsicanget.add(maxmult);
		}
		else if(thekingpos==2)
		{
			positionofhigherpointsicanget.add(mydualsbestchoice.elementAt(maxdualpos));
			higherpointsicanget.add(maxdual);
		}
		else if(thekingpos==3)
		{
			temp=takeTheOneThatHasTheBiggerNumberNear(maxpositions, thegame);
			if(temp!=-1)
			{
				positionofhigherpointsicanget.add(temp);
			}
			else
			{
				temp=pareAutoPouEinaiDiplaMou(maxkoino, maxpositions, thegame);
				if(temp!=-1)
				{
					positionofhigherpointsicanget.add(temp);
				}
				else
				{
					positionofhigherpointsicanget.add(maxpositions.elementAt(0));
				}
			}
			higherpointsicanget.add(maxkoino);
		}
			
	}
	
	public boolean einaiToSunexomenoKotsarismaMegaluteroApoToSuntelesthPouExwPei(Vector<Integer> higherpointshecanget, Vector<Integer> improvedmykotsarisma, Vector<Integer> mymultbestchoice, Vector<Integer> mydualsbestchoice, Vector<Integer> kerdosofmykotsarisma, Vector<Integer> profitofmymultbestchoice, Vector<Integer> profitofmydualswithbestchoice, Vector<Integer> higherpointsicanget, Vector<Integer> positionofhigherpointsicanget, Vector<Integer> maxpositions, FacilityGameAPI thegame)
	{
		int[] tobecompared=new int[4];
		int n = thegame.getN();
		int maxkerdh=-1;
		int maxkerdpos=-1;
		int maxmult=-1;
		int maxmultpos=-1;
		int maxdual=-1;
		int maxdualpos=-1;
		int maxkoino=-1;
		int maxkoinopos=-1;
		int theking=-1;
		int thekingpos=-1;
		boolean sumferon=true;
		
		for(int i=0; i<kerdosofmykotsarisma.size(); i++)
		{
			if(kerdosofmykotsarisma.elementAt(i)>maxkerdh)
			{
				maxkerdh=kerdosofmykotsarisma.elementAt(i);
				maxkerdpos=i;
			}
		}
		tobecompared[0]=maxkerdh;
		
		for(int i=0; i<profitofmymultbestchoice.size(); i++)
		{
			if(profitofmymultbestchoice.elementAt(i)>maxmult)
			{
				maxmult=profitofmymultbestchoice.elementAt(i);
				maxmultpos=i;
			}
		}		
		tobecompared[1]=maxmult;		
		
		for(int i=0; i<profitofmydualswithbestchoice.size(); i++)
		{
			if(profitofmydualswithbestchoice.elementAt(i)>maxdual)
			{
				maxdual=profitofmydualswithbestchoice.elementAt(i);
				maxdualpos=i;
			}
		}	
		tobecompared[2]=maxdual;
		
		for(int i=0; i<n; i++)
		{
			if(thegame.getStatus(i)==EnumFacilityStatus.FREE)
			{
				if(thegame.getValue(i)>maxkoino)                   
				{
					maxkoino=thegame.getValue(i);       
					maxkoinopos=i;
				}
			}
		}
		tobecompared[3]=maxkoino;

		for(int i=0; i<4; i++)
		{
			if(tobecompared[i]!=-1)
			{
				if(tobecompared[i]>theking)
				{
					theking=tobecompared[i];
					thekingpos=i;
				}
			}
		}
		
		if(thekingpos==0)
		{
			if(maxkerdh<4*maxkoino)
			{
				sumferon=false;
			}
		}
		else if(thekingpos==1)
		{
			if(maxmult<2*maxkoino)
			{
				sumferon=false;
			}
		}
		else if(thekingpos==2)
		{
			if(maxdual<5.6*maxkoino)
			{
				sumferon=false;
			}
		}
		
		return sumferon;
	}
	
	public void hisMoveForMaxPoints(Vector<Integer> improvedotherkotsarisma, Vector<Integer> profitofothermultbestchoice, Vector<Integer> otherdualsbestchoice, Vector<Integer> kerdosofotherkotsarisma, Vector<Integer> othermultbestchoice, Vector<Integer> profitofotherdualswithbestchoice, Vector<Integer> higherpointshecanget, Vector<Integer> positionofhigherpointshecanget)
	{
		int[] tobecompared=new int[3];
		int maxkerdh=-1;
		int maxkerdpos=-1;
		int maxmult=-1;
		int maxmultpos=-1;
		int maxdual=-1;
		int maxdualpos=-1;
		int theking=-1;
		int thekingpos=-1;
		
		for(int i=0; i<kerdosofotherkotsarisma.size(); i++)
		{
			if(kerdosofotherkotsarisma.elementAt(i)>maxkerdh)
			{
				maxkerdh=kerdosofotherkotsarisma.elementAt(i);
				maxkerdpos=i;
			}
		}
		tobecompared[0]=maxkerdh;
		
		for(int i=0; i<profitofothermultbestchoice.size(); i++)
		{
			if(profitofothermultbestchoice.elementAt(i)>maxmult)
			{
				maxmult=profitofothermultbestchoice.elementAt(i);
				maxmultpos=i;
			}
		}		
		tobecompared[1]=maxmult;		
		
		for(int i=0; i<profitofotherdualswithbestchoice.size(); i++)
		{
			if(profitofotherdualswithbestchoice.elementAt(i)>maxdual)
			{
				maxdual=profitofotherdualswithbestchoice.elementAt(i);
				maxdualpos=i;
			}
		}	
		tobecompared[2]=maxdual;

		for(int i=0; i<3; i++)
		{
			if(tobecompared[i]!=-1)
			{
				if(tobecompared[i]>theking)
				{
					theking=tobecompared[i];
					thekingpos=i;
				}
			}
		}
		
		if(thekingpos==0)
		{
			positionofhigherpointshecanget.add(improvedotherkotsarisma.elementAt(maxkerdpos));
			higherpointshecanget.add(maxkerdh);
		}
		else if(thekingpos==1)
		{
			positionofhigherpointshecanget.add(othermultbestchoice.elementAt(maxmultpos));
			higherpointshecanget.add(maxmult);
		}
		else if(thekingpos==2)
		{
			positionofhigherpointshecanget.add(otherdualsbestchoice.elementAt(maxdualpos));
			higherpointshecanget.add(maxdual);
		}
		
	}
	
	public boolean aksizeiNaTonBlockarw(int n, int currentmax, Vector<Integer> improvedotherkotsarisma, Vector<Integer> profitofothermultbestchoice, Vector<Integer> otherdualsbestchoice, Vector<Integer> kerdosofotherkotsarisma, Vector<Integer> othermultbestchoice, Vector<Integer> profitofotherdualswithbestchoice, Vector<Integer> higherpointshecanget, Vector<Integer> positionofhigherpointshecanget)
	{
		int[] tobecompared=new int[3];
		int maxkerdh=-1;
		int maxkerdpos=-1;
		int maxmult=-1;
		int maxmultpos=-1;
		int maxdual=-1;
		int maxdualpos=-1;
		int theking=-1;
		int thekingpos=-1;
		boolean aksizei=true;
		
		for(int i=0; i<kerdosofotherkotsarisma.size(); i++)
		{
			if(kerdosofotherkotsarisma.elementAt(i)>maxkerdh)
			{
				maxkerdh=kerdosofotherkotsarisma.elementAt(i);
				maxkerdpos=i;
			}
		}
		tobecompared[0]=maxkerdh;
		
		for(int i=0; i<profitofothermultbestchoice.size(); i++)
		{
			if(profitofothermultbestchoice.elementAt(i)>maxmult)
			{
				maxmult=profitofothermultbestchoice.elementAt(i);
				maxmultpos=i;
			}
		}		
		tobecompared[1]=maxmult;		
		
		for(int i=0; i<profitofotherdualswithbestchoice.size(); i++)
		{
			if(profitofotherdualswithbestchoice.elementAt(i)>maxdual)
			{
				maxdual=profitofotherdualswithbestchoice.elementAt(i);
				maxdualpos=i;
			}
		}	
		tobecompared[2]=maxdual;

		for(int i=0; i<3; i++)
		{
			if(tobecompared[i]!=-1)
			{
				if(tobecompared[i]>theking)
				{
					theking=tobecompared[i];
					thekingpos=i;
				}
			}
		}
		
		if(n>150)
		{
			if(thekingpos==0)
			{
				if(maxkerdh<4*currentmax)
				{
					aksizei=false;
				}
			}
			else if(thekingpos==1)
			{
				if(maxmult<2*currentmax)
				{
					aksizei=false;
				}
			}
			else if(thekingpos==2)
			{
				if(maxdual<5.6*currentmax)
				{
					aksizei=false;
				}
			}
		}
		else
		{
			if(thekingpos==0)
			{
				if(maxkerdh<4*currentmax)
				{
					aksizei=false;
				}
			}
			else if(thekingpos==1)
			{
				if(maxmult<7*currentmax)
				{
					aksizei=false;
				}
			}
			else if(thekingpos==2)
			{
				if(maxdual<5*currentmax)
				{
					aksizei=false;
				}
			}
		}
		
		return aksizei;
	}
	
	public boolean doIHaveContiniousOrKotsarismataOrDuals(Vector<Integer> mydualsbestchoice, Vector<Integer> mymultbestchoice, Vector<Integer> improvedmykotsarisma)
	{
		
		int multflag=0, dualflag=0, kotsflag=0;  //1 means they exist
		
		if(mydualsbestchoice.isEmpty()==false)
		{
			for(int i=0; i<mydualsbestchoice.size(); i++)
			{
				if(mydualsbestchoice.elementAt(i)!=-1)
				{
					dualflag=1;
					break;
				}
			}
		}

		if(mymultbestchoice.isEmpty()==false)
		{
			for(int i=0; i<mymultbestchoice.size(); i++)
			{
				if(mymultbestchoice.elementAt(i)!=-1)
				{
					multflag=1;
					break;
				}
			}
		}
		
		if(improvedmykotsarisma.isEmpty()==false) kotsflag=1;
		
		if(multflag==0 && dualflag==0 && kotsflag==0)
		{
			return false;
		}
		else
		{
			return true;
		}	
	}
	
	public boolean doesHeHaveContiniousOrKotsarismataOrDuals(Vector<Integer> otherdualsbestchoice, Vector<Integer> othermultbestchoice, Vector<Integer> improvedotherkotsarisma)
	{
		
		int multflag=0, dualflag=0, kotsflag=0;  //1 means they exist
		
		if(otherdualsbestchoice.isEmpty()==false)
		{
			for(int i=0; i<otherdualsbestchoice.size(); i++)
			{
				if(otherdualsbestchoice.elementAt(i)!=-1)
				{
					dualflag=1;
					break;
				}
			}
		}

		if(othermultbestchoice.isEmpty()==false)
		{
			for(int i=0; i<othermultbestchoice.size(); i++)
			{
				if(othermultbestchoice.elementAt(i)!=-1)
				{
					multflag=1;
					break;
				}
			}
		}
		
		if(improvedotherkotsarisma.isEmpty()==false) kotsflag=1;
		
		if(multflag==0 && dualflag==0 && kotsflag==0)
		{
			return false;
		}
		else
		{
			return true;
		}
		
	}
	
	public int chooseInMaxNeighborhood(Vector<Integer> maxpositions, int maxneighborhood, Vector<Integer> geitoniessum, Vector<Integer> geitonies, Vector<Integer> endofgeitonies, Vector<Integer> maxgeitonies, Vector<Integer> kanonik, Vector<Integer> posageitniazoun, int numberofmaxgeitonies, int currentmax, FacilityGameAPI thegame)
	{
		int n = thegame.getN();
		int move=-1;
		int neighborhoodchosen=-1;
		int stoixeioofneighborhoodchosen;		
		int howmanyplacesafter;
		int maxsum=-1;
		int maxsumpos;
		int leftsideofgeitonia=-1, rightsideofgeitonia=-1;
		int lefttemp;
		int righttemp;
		
		for(int i=0; i<kanonik.size(); i++)  //from kanonik i have here only the neighborhoods with the most elements, so i find maxsum
		{
			if(geitoniessum.elementAt(kanonik.elementAt(i))>maxsum)
			{
				maxsum=geitoniessum.elementAt(kanonik.elementAt(i));
				maxsumpos=kanonik.elementAt(i);
			}
		}
		
		for(int i=0; i<kanonik.size(); i++)  
		{
			if(geitoniessum.elementAt(kanonik.elementAt(i))==maxsum)
			{
				neighborhoodchosen=kanonik.elementAt(i);
			}
		}
		
		
		if(posageitniazoun.elementAt(neighborhoodchosen)==2)
		{
			if(thegame.getValue(geitonies.elementAt(neighborhoodchosen))>=thegame.getValue(endofgeitonies.elementAt(neighborhoodchosen)))
			{
				if(thegame.getStatus(geitonies.elementAt(neighborhoodchosen))==EnumFacilityStatus.FREE)
				{
					move=geitonies.elementAt(neighborhoodchosen);
					return move;
				}
				else
				{
					return move=endofgeitonies.elementAt(neighborhoodchosen);
				}
			}
			else
			{
				if(thegame.getStatus(endofgeitonies.elementAt(neighborhoodchosen))==EnumFacilityStatus.FREE)
				{
					move=endofgeitonies.elementAt(neighborhoodchosen);
					return move;
				}
				else
				{
					return move=geitonies.elementAt(neighborhoodchosen);
				}
			}
		}
		
		stoixeioofneighborhoodchosen=Math.round((float)posageitniazoun.elementAt(neighborhoodchosen)/2);
		if(posageitniazoun.elementAt(neighborhoodchosen)%2==1) stoixeioofneighborhoodchosen--;		
		howmanyplacesafter=globalPositionOfNeighborhoodElement(currentmax, neighborhoodchosen, stoixeioofneighborhoodchosen, geitonies, thegame);
		
		if(belongsToMe(geitonies.elementAt(neighborhoodchosen), thegame)) //left edge taken holded by me
		{
			if(geitonies.elementAt(neighborhoodchosen)+3<=n-1)
			{
				if(thegame.getValue(geitonies.elementAt(neighborhoodchosen)+3)>=currentmax)
				{
					if(geitonies.elementAt(neighborhoodchosen)+4<=endofgeitonies.elementAt(neighborhoodchosen))  //existence
					{
						if(geitonies.elementAt(neighborhoodchosen)+6<=endofgeitonies.elementAt(neighborhoodchosen))  //existence
						{
							if(thegame.getStatus(geitonies.elementAt(neighborhoodchosen)+4)==EnumFacilityStatus.FREE && thegame.getStatus(geitonies.elementAt(neighborhoodchosen)+6)==EnumFacilityStatus.FREE)  //free
							{
								if(thegame.getValue(geitonies.elementAt(neighborhoodchosen)+4)>=thegame.getValue(geitonies.elementAt(neighborhoodchosen)+6))
								{
									move=geitonies.elementAt(neighborhoodchosen)+4;
								}
								else
								{
									move=geitonies.elementAt(neighborhoodchosen)+6;
								}
							}
							else if(thegame.getStatus(geitonies.elementAt(neighborhoodchosen)+4)==EnumFacilityStatus.FREE && thegame.getStatus(geitonies.elementAt(neighborhoodchosen)+6)!=EnumFacilityStatus.FREE)
							{
								move=geitonies.elementAt(neighborhoodchosen)+4;
							}
							else if(thegame.getStatus(geitonies.elementAt(neighborhoodchosen)+4)!=EnumFacilityStatus.FREE && thegame.getStatus(geitonies.elementAt(neighborhoodchosen)+6)==EnumFacilityStatus.FREE)
							{
								move=geitonies.elementAt(neighborhoodchosen)+6;
							}
							else
							{
								if(thegame.getStatus(geitonies.elementAt(neighborhoodchosen)+2)==EnumFacilityStatus.FREE) 
								{
									move=geitonies.elementAt(neighborhoodchosen)+2;
								}
								else
								{
									move=takeTheOneThatHasTheBiggerNumberNear(maxpositions, thegame);
									
									if(move==-1)
									{
										move=pareAutoPouEinaiDiplaMou(currentmax, maxpositions, thegame);
										if(move==-1)
										{
											move=maxpositions.elementAt(0);
										}
									}
								}
							}
						}
						else
						{
							if(thegame.getStatus(geitonies.elementAt(neighborhoodchosen)+4)==EnumFacilityStatus.FREE)
							{
								move=geitonies.elementAt(neighborhoodchosen)+4;
							}
							else
							{
								if(thegame.getValue(geitonies.elementAt(neighborhoodchosen)+3)>thegame.getValue(geitonies.elementAt(neighborhoodchosen)+2) && thegame.getStatus(geitonies.elementAt(neighborhoodchosen)+3)==EnumFacilityStatus.FREE)
								{
									move=geitonies.elementAt(neighborhoodchosen)+3;
								}
								else
								{
									if(thegame.getStatus(geitonies.elementAt(neighborhoodchosen)+2)==EnumFacilityStatus.FREE) 
									{
										move=geitonies.elementAt(neighborhoodchosen)+2;
									}
									else
									{
										move=takeTheOneThatHasTheBiggerNumberNear(maxpositions, thegame);
										
										if(move==-1)
										{
											move=pareAutoPouEinaiDiplaMou(currentmax, maxpositions, thegame);
											if(move==-1)
											{
												move=maxpositions.elementAt(0);
											}
										}
									}
								}
							}
						}
					}
					else
					{
						if(thegame.getValue(geitonies.elementAt(neighborhoodchosen)+3)>thegame.getValue(geitonies.elementAt(neighborhoodchosen)+2) && thegame.getStatus(geitonies.elementAt(neighborhoodchosen)+3)==EnumFacilityStatus.FREE)
						{
							move=geitonies.elementAt(neighborhoodchosen)+3;
						}
						else
						{
							if(thegame.getStatus(geitonies.elementAt(neighborhoodchosen)+2)==EnumFacilityStatus.FREE) 
							{
								move=geitonies.elementAt(neighborhoodchosen)+2;
							}
							else
							{
								move=takeTheOneThatHasTheBiggerNumberNear(maxpositions, thegame);
								
								if(move==-1)
								{
									move=pareAutoPouEinaiDiplaMou(currentmax, maxpositions, thegame);
									if(move==-1)
									{
										move=maxpositions.elementAt(0);
									}
								}
							}
						}
					}
			    }
				else
				{
					if(geitonies.elementAt(neighborhoodchosen)+4<=endofgeitonies.elementAt(neighborhoodchosen))
					{
						if(thegame.getStatus(geitonies.elementAt(neighborhoodchosen)+4)==EnumFacilityStatus.FREE) 
						{
							move=geitonies.elementAt(neighborhoodchosen)+4;
						}
						else
						{
							if(thegame.getStatus(geitonies.elementAt(neighborhoodchosen)+2)==EnumFacilityStatus.FREE) 
							{
								move=geitonies.elementAt(neighborhoodchosen)+2;
							}
							else
							{
								move=takeTheOneThatHasTheBiggerNumberNear(maxpositions, thegame);
								
								if(move==-1)
								{
									move=pareAutoPouEinaiDiplaMou(currentmax, maxpositions, thegame);
									if(move==-1)
									{
										move=maxpositions.elementAt(0);
									}
								}
							}
						}
					}
					else
					{
						if(thegame.getStatus(geitonies.elementAt(neighborhoodchosen)+2)==EnumFacilityStatus.FREE) 
						{
							move=geitonies.elementAt(neighborhoodchosen)+2;
						}
						else
						{
							move=takeTheOneThatHasTheBiggerNumberNear(maxpositions, thegame);
							
							if(move==-1)
							{
								move=pareAutoPouEinaiDiplaMou(currentmax, maxpositions, thegame);
								if(move==-1)
								{
									move=maxpositions.elementAt(0);
								}
							}
						}
					}
				}
			}	
			else
			{
				if(thegame.getStatus(geitonies.elementAt(neighborhoodchosen)+2)==EnumFacilityStatus.FREE) 
				{
					move=geitonies.elementAt(neighborhoodchosen)+2;
				}
				else
				{
					move=takeTheOneThatHasTheBiggerNumberNear(maxpositions, thegame);
					if(move==-1)
					{
						move=pareAutoPouEinaiDiplaMou(currentmax, maxpositions, thegame);
						
						if(move==-1)
						{
							move=maxpositions.elementAt(0);
						}
					}
				}
			}
		}
		else if(belongsToMe(endofgeitonies.elementAt(neighborhoodchosen), thegame))
		{
			if(geitonies.elementAt(neighborhoodchosen)-3>=0)
			{
				if(thegame.getValue(endofgeitonies.elementAt(neighborhoodchosen)-3)>=currentmax)
				{
					if(endofgeitonies.elementAt(neighborhoodchosen)-4>=geitonies.elementAt(neighborhoodchosen))  //uparksh
					{
						if(endofgeitonies.elementAt(neighborhoodchosen)-6>=geitonies.elementAt(neighborhoodchosen))  //uparksh
						{
							if(thegame.getStatus(endofgeitonies.elementAt(neighborhoodchosen)-4)==EnumFacilityStatus.FREE && thegame.getStatus(endofgeitonies.elementAt(neighborhoodchosen)-6)==EnumFacilityStatus.FREE)  //free
							{
								if(thegame.getValue(endofgeitonies.elementAt(neighborhoodchosen)-4)>=thegame.getValue(endofgeitonies.elementAt(neighborhoodchosen)-6))
								{
									move=endofgeitonies.elementAt(neighborhoodchosen)-4;
								}
								else
								{
									move=endofgeitonies.elementAt(neighborhoodchosen)-6;
								}
							}
							else if(thegame.getStatus(endofgeitonies.elementAt(neighborhoodchosen)-4)==EnumFacilityStatus.FREE && thegame.getStatus(endofgeitonies.elementAt(neighborhoodchosen)-6)!=EnumFacilityStatus.FREE)
							{
								move=endofgeitonies.elementAt(neighborhoodchosen)-4;
							}
							else if(thegame.getStatus(endofgeitonies.elementAt(neighborhoodchosen)-4)!=EnumFacilityStatus.FREE && thegame.getStatus(endofgeitonies.elementAt(neighborhoodchosen)-6)==EnumFacilityStatus.FREE)
							{
								move=endofgeitonies.elementAt(neighborhoodchosen)-6;
							}
							else
							{
								if(thegame.getStatus(geitonies.elementAt(neighborhoodchosen)-2)==EnumFacilityStatus.FREE) 
								{
									move=geitonies.elementAt(neighborhoodchosen)-2;
								}
								else
								{
									move=takeTheOneThatHasTheBiggerNumberNear(maxpositions, thegame);
									
									if(move==-1)
									{
										move=pareAutoPouEinaiDiplaMou(currentmax, maxpositions, thegame);
										if(move==-1)
										{
											move=maxpositions.elementAt(0);
										}
									}
								}
							}
						}
						else
						{
							if(thegame.getStatus(endofgeitonies.elementAt(neighborhoodchosen)-4)==EnumFacilityStatus.FREE)
							{
								move=endofgeitonies.elementAt(neighborhoodchosen)-4;
							}
							else
							{
								if(thegame.getValue(geitonies.elementAt(neighborhoodchosen)-3)>thegame.getValue(geitonies.elementAt(neighborhoodchosen)-2) && thegame.getStatus(geitonies.elementAt(neighborhoodchosen)-3)==EnumFacilityStatus.FREE)
								{
									move=endofgeitonies.elementAt(neighborhoodchosen)-3;
								}
								else
								{
									if(thegame.getStatus(geitonies.elementAt(neighborhoodchosen)-2)==EnumFacilityStatus.FREE) 
									{
										move=geitonies.elementAt(neighborhoodchosen)-2;
									}
									else
									{
										move=takeTheOneThatHasTheBiggerNumberNear(maxpositions, thegame);
										
										if(move==-1)
										{
											move=pareAutoPouEinaiDiplaMou(currentmax, maxpositions, thegame);
											if(move==-1)
											{
												move=maxpositions.elementAt(0);
											}
										}
									}
								}
							}
						}
					}
					else
					{
						if(thegame.getValue(geitonies.elementAt(neighborhoodchosen)-3)>thegame.getValue(geitonies.elementAt(neighborhoodchosen)-2) && thegame.getStatus(geitonies.elementAt(neighborhoodchosen)-3)==EnumFacilityStatus.FREE)
						{
							move=endofgeitonies.elementAt(neighborhoodchosen)-3;
						}
						else
						{
							if(thegame.getStatus(geitonies.elementAt(neighborhoodchosen)-2)==EnumFacilityStatus.FREE) 
							{
								move=geitonies.elementAt(neighborhoodchosen)-2;
							}
							else
							{
								move=takeTheOneThatHasTheBiggerNumberNear(maxpositions, thegame);
								
								if(move==-1)
								{
									move=pareAutoPouEinaiDiplaMou(currentmax, maxpositions, thegame);
									if(move==-1)
									{
										move=maxpositions.elementAt(0);
									}
								}
							}
						}
					}
				}
			else
			{
				if(endofgeitonies.elementAt(neighborhoodchosen)-4>=geitonies.elementAt(neighborhoodchosen))
				{
					if(thegame.getStatus(endofgeitonies.elementAt(neighborhoodchosen)-4)==EnumFacilityStatus.FREE) 
					{
						move=endofgeitonies.elementAt(neighborhoodchosen)-4;
					}
					else
					{
						if(thegame.getStatus(geitonies.elementAt(neighborhoodchosen)-2)==EnumFacilityStatus.FREE) 
						{
							move=geitonies.elementAt(neighborhoodchosen)-2;
						}
						else
						{
							move=takeTheOneThatHasTheBiggerNumberNear(maxpositions, thegame);
							
							if(move==-1)
							{
								move=pareAutoPouEinaiDiplaMou(currentmax, maxpositions, thegame);
								if(move==-1)
								{
									move=maxpositions.elementAt(0);
								}
							}
						}
					}
				}
				else
				{
					if(thegame.getStatus(geitonies.elementAt(neighborhoodchosen)-2)==EnumFacilityStatus.FREE) 
					{
						move=geitonies.elementAt(neighborhoodchosen)-2;
					}
					else
					{
						move=takeTheOneThatHasTheBiggerNumberNear(maxpositions, thegame);
						
						if(move==-1)
						{
							move=pareAutoPouEinaiDiplaMou(currentmax, maxpositions, thegame);
							if(move==-1)
							{
								move=maxpositions.elementAt(0);
							}
						}
					}
				}
			}
		  }		
		  else
		  {
			  if(thegame.getStatus(geitonies.elementAt(neighborhoodchosen)-2)==EnumFacilityStatus.FREE) 
				{
					move=geitonies.elementAt(neighborhoodchosen)-2;
				}
				else
				{
					move=takeTheOneThatHasTheBiggerNumberNear(maxpositions, thegame);
					
					if(move==-1)
					{
						move=pareAutoPouEinaiDiplaMou(currentmax, maxpositions, thegame);
						if(move==-1)
						{
							move=maxpositions.elementAt(0);
						}
					}
				}
		  }
		}
		else if(thegame.getStatus(howmanyplacesafter)==EnumFacilityStatus.FREE)
		{
			move=howmanyplacesafter;
		}
		else         
		{   
			if(posageitniazoun.elementAt(neighborhoodchosen)==3)
			{
				lefttemp=bestOfFour(geitonies.elementAt(neighborhoodchosen)-2, geitonies.elementAt(neighborhoodchosen)-3, -1, -1, thegame); //the temp because bestOfFour may return -1 if it hasn't found any free or to exist
				righttemp=bestOfFour(endofgeitonies.elementAt(neighborhoodchosen)+2, endofgeitonies.elementAt(neighborhoodchosen)+3, -1, -1, thegame);
				if(lefttemp!=-1)
				{
					leftsideofgeitonia=thegame.getValue(bestOfFour(geitonies.elementAt(neighborhoodchosen)-2, geitonies.elementAt(neighborhoodchosen)-3, -1, -1, thegame));
				}
				if(righttemp!=-1)
				{
					rightsideofgeitonia=thegame.getValue(bestOfFour(endofgeitonies.elementAt(neighborhoodchosen)+2, endofgeitonies.elementAt(neighborhoodchosen)+3, -1, -1, thegame));
				}
				if(leftsideofgeitonia!=-1 || rightsideofgeitonia!=-1)
				{
					if(leftsideofgeitonia>=rightsideofgeitonia)
					{
						if(thegame.getStatus(geitonies.elementAt(neighborhoodchosen))==EnumFacilityStatus.FREE)
						{
							move=geitonies.elementAt(neighborhoodchosen);
						}
						else
						{
							move=pareAutoPouEinaiDiplaMou(currentmax, maxpositions, thegame);
							if(move==-1) move=takeTheOneThatHasTheBiggerNumberNear(maxpositions, thegame);
							if(move==-1) move=maxpositions.elementAt(0);
						}
					}
					else
					{
						if(thegame.getStatus(endofgeitonies.elementAt(neighborhoodchosen))==EnumFacilityStatus.FREE)
						{
							move=endofgeitonies.elementAt(neighborhoodchosen);
						}
						else
						{
							move=pareAutoPouEinaiDiplaMou(currentmax, maxpositions, thegame);
							if(move==-1) move=takeTheOneThatHasTheBiggerNumberNear(maxpositions, thegame);
							if(move==-1) move=maxpositions.elementAt(0);
						}
					}
				}
				else
				{
					move=bestOfFour(howmanyplacesafter+2, howmanyplacesafter-2, howmanyplacesafter+3, howmanyplacesafter-3, thegame);
					if(move==-1) move=pareAutoPouEinaiDiplaMou(currentmax, maxpositions, thegame);
					if(move==-1) move=takeTheOneThatHasTheBiggerNumberNear(maxpositions, thegame);
					if(move==-1) move=maxpositions.elementAt(0);
				}
			}
			else
			{
				move=bestOfFour(howmanyplacesafter+2, howmanyplacesafter-2, howmanyplacesafter+3, howmanyplacesafter-3, thegame);
				if(move==-1) move=pareAutoPouEinaiDiplaMou(currentmax, maxpositions, thegame);
				if(move==-1) move=takeTheOneThatHasTheBiggerNumberNear(maxpositions, thegame);
				if(move==-1) move=maxpositions.elementAt(0);
			}
		}
		
		return move;	
	}
	
	public boolean exeiOAllosPerissoteraSeKapoiaGeitonia(Vector<Integer> exwmonossegeitonia, Vector<Integer> exeimonossegeitonia, Vector<Integer> geitonies, Vector<Integer> endofgeitonies, FacilityGameAPI thegame)
	{
		Vector<Integer> posasekathegeitonia = new Vector<Integer>();
		Vector<Integer> ends = new Vector<Integer>();
		Vector<Integer> myposasekathegeitonia = new Vector<Integer>();
		Vector<Integer> myends = new Vector<Integer>();
		int sum=0;
		int damax=0;
		int mydamax=0;
		
		for(int i=0; i<geitonies.size(); i++)
		{
			for(int j=0; j<exeimonossegeitonia.size(); j++)
			{
				if(exeimonossegeitonia.elementAt(j).equals(geitonies.elementAt(i)))
				{
					ends.add(endofgeitonies.elementAt(i));
				}
			}
		}
		
		
		for(int i=0; i<exeimonossegeitonia.size(); i++)
		{
			sum=0;
			for(int k=exeimonossegeitonia.elementAt(i); k<=ends.elementAt(i); k++)   //fail
			{
				if(belongsToOther(k, thegame))
				{
					sum++;
				}
			}
			posasekathegeitonia.add(sum);
		}
		

		for(int i=0; i<exeimonossegeitonia.size(); i++)
		{
			if(posasekathegeitonia.elementAt(i)>damax)
			{
				damax=posasekathegeitonia.elementAt(i);
			}
		}
		
		////////////////////////////////////////////////////////////////////////////
		
		for(int i=0; i<geitonies.size(); i++)
		{
			for(int j=0; j<exwmonossegeitonia.size(); j++)
			{
				if(exwmonossegeitonia.elementAt(j).equals(geitonies.elementAt(i)))
				{
					myends.add(endofgeitonies.elementAt(i));
				}
			}
		}
		
		
		for(int i=0; i<exwmonossegeitonia.size(); i++)
		{
			sum=0;
			for(int k=exwmonossegeitonia.elementAt(i); k<=myends.elementAt(i); k++)
			{
				if(belongsToMe(k, thegame))
				{
					sum++;
				}
			}
			myposasekathegeitonia.add(sum);
		}
		
		
		for(int i=0; i<exwmonossegeitonia.size(); i++)
		{
			if(myposasekathegeitonia.elementAt(i)>mydamax)
			{
				mydamax=myposasekathegeitonia.elementAt(i);
			}
		}
		
		return damax>mydamax;
	}
	
	public int blockHimWhenHeIsAlone(int currentmax, Vector<Integer> exeimonossegeitonia, Vector<Integer> geitonies, Vector<Integer> posageitniazoun, Vector<Integer> endofgeitonies, FacilityGameAPI thegame)
	{
		int move=-1;
		int n = thegame.getN();
		Vector<Integer> posasekathegeitonia = new Vector<Integer>();
		Vector<Integer> ends = new Vector<Integer>();
		int sum=0;
		int stoixeioofneighborhoodchosen;
		int neighborhoodchosen=-1;
		int kentro;
		                                           //from here a check starts to see which neighborhood has the most elements and take it 
		for(int i=0; i<geitonies.size(); i++)   
		{
			for(int j=0; j<exeimonossegeitonia.size(); j++)
			{
				if(exeimonossegeitonia.elementAt(j).equals(geitonies.elementAt(i)))
				{
					ends.add(endofgeitonies.elementAt(i));
				}
			}
		}

		
		for(int i=0; i<exeimonossegeitonia.size(); i++)
		{
			sum=0;
			for(int k=exeimonossegeitonia.elementAt(i); k<=ends.elementAt(i); k++)
			{
				if(belongsToOther(k, thegame))
				{
					sum++;
				}
			}
			posasekathegeitonia.add(sum);
		}
		
		
		int damax=0;
		int damaxpos=-1;
		for(int i=0; i<exeimonossegeitonia.size(); i++)
		{
			if(posasekathegeitonia.elementAt(i)>damax)
			{
				damax=posasekathegeitonia.elementAt(i);
				damaxpos=i;
			}
		}                                           //until here is the check
		
		for(int i=0; i<geitonies.size(); i++)
		{
			if(exeimonossegeitonia.elementAt(damaxpos).equals(geitonies.elementAt(i)))
			{
				neighborhoodchosen=i;
			}
		}
		
		stoixeioofneighborhoodchosen=Math.round((float)posageitniazoun.elementAt(neighborhoodchosen)/2);		
		if(posageitniazoun.elementAt(neighborhoodchosen)%2==1) stoixeioofneighborhoodchosen--;
		kentro=globalPositionOfNeighborhoodElement(currentmax, neighborhoodchosen, stoixeioofneighborhoodchosen, geitonies, thegame);
		
		if(belongsToOther(exeimonossegeitonia.elementAt(damaxpos)+0, thegame))
		{
			move=bestOfFour(exeimonossegeitonia.elementAt(damaxpos)-2, exeimonossegeitonia.elementAt(damaxpos)-3, exeimonossegeitonia.elementAt(damaxpos)+2, exeimonossegeitonia.elementAt(damaxpos)+3, thegame);
		}
		else if(belongsToOther(ends.elementAt(damaxpos), thegame))
		{
			move=bestOfFour(ends.elementAt(damaxpos)-2, ends.elementAt(damaxpos)-3, ends.elementAt(damaxpos)+2, ends.elementAt(damaxpos)+3, thegame);
		}
		else if(belongsToOther(kentro, thegame))
		{
			move=bestOfFour(kentro-2, kentro-3, kentro+2, kentro+3, thegame);
		}
		else if(thegame.getStatus(kentro)==EnumFacilityStatus.FREE)
		{
			move=kentro;
		}
		
		if(move==-1)//If it's not one of the above cases or bestOfFour return -1 choose a random one
		{
			for(int i=exeimonossegeitonia.elementAt(damaxpos); i<=ends.elementAt(damaxpos); i++)
			{
				if(thegame.getStatus(i)==EnumFacilityStatus.FREE) move=i;
			}
		}
		
		
		return move;
	}
	
	public int chooseFromMyOnes(Vector<Integer> myones, Vector<Integer> myonesbestchoice, FacilityGameAPI game)
	{
		int maxchoice=-1;
		int pos=-1;
		
		for(int i=0; i<myones.size(); i++)
		{
			if(myonesbestchoice.elementAt(i)!=-1)
			{
				if(game.getValue(myonesbestchoice.elementAt(i))>maxchoice)
				{
					maxchoice=myonesbestchoice.elementAt(i);
					pos=myonesbestchoice.elementAt(i);
				}
			}
		}  
		
		return pos;
	}
	
	public boolean atLeastOneMyOneIsFree(Vector<Integer> myonesbestchoice, FacilityGameAPI game)
	{
		for(int i=0; i<myonesbestchoice.size(); i++)
		{
			if(myonesbestchoice.elementAt(i)!=-1) return true;
		}
		
		return false;
	}
	
	public int takeTheOneThatHasTheBiggerNumberNear(Vector<Integer> maxpositions, FacilityGameAPI game)
	{
		Vector<Integer> highers = new Vector<Integer>();
		int maxhigher=-1;
		int poshigher=-1;
		
		for(int i=0; i<maxpositions.size(); i++)
		{
			highers.add(bestOfFour(maxpositions.elementAt(i)-3, maxpositions.elementAt(i)-2, maxpositions.elementAt(i)+2, maxpositions.elementAt(i)+3, game));
		}
		
		for(int i=0; i<maxpositions.size(); i++)
		{
			if(highers.elementAt(i)!=-1)
			{
				if(game.getValue(highers.elementAt(i))>=maxhigher)
				{
					maxhigher=game.getValue(highers.elementAt(i));
					poshigher=maxpositions.elementAt(i);
				}
			}
		}
			
		return poshigher;	
	}
	
	public int pareAutoPouEinaiDiplaMou(int currentmax, Vector<Integer> maxpositions, FacilityGameAPI game)
	{
		int n = game.getN();
		
		for(int i=0; i<n; i++)
		{
			if(game.getValue(i)==currentmax && game.getStatus(i)==EnumFacilityStatus.FREE)
			{
				if(exists(i-2, n))
				{
					if(exists(i-3, n))
					{
						if(belongsToMe(i-3, game) || belongsToMe(i-2, game))
						{
							return i;
						}
					}
					else
					{
						if(belongsToMe(i-2, game))
						{
							return i;
						}
					}
				}
				
				if(exists(i+2, n))
				{
					if(exists(i+3, n))
					{
						if(belongsToMe(i+3, game) || belongsToMe(i+2, game))
						{
							return i;
						}
					}
					else
					{
						if(belongsToMe(i+2, game))
						{
							return i;
						}
					}
				}
			}
		}
		
		return -1; //None of mine was found near max
	}
	
	public int pareAutoPouEinaiDiplaTou(int currentmax, Vector<Integer> maxpositions, FacilityGameAPI game)
	{
		int n = game.getN();
		
		for(int i=0; i<n; i++)
		{
			if(game.getValue(i)==currentmax && game.getStatus(i)==EnumFacilityStatus.FREE)
			{
				if(exists(i-2, n))
				{
					if(exists(i-3, n))
					{
						if(belongsToOther(i-3, game) || belongsToOther(i-2, game))
						{
							return i;
						}
					}
					else
					{
						if(belongsToOther(i-2, game))
						{
							return i;
						}
					}
				}
				
				if(exists(i+2, n))
				{
					if(exists(i+3, n))
					{
						if(belongsToOther(i+3, game) || belongsToOther(i+2, game))
						{
							return i;
						}
					}
					else
					{
						if(belongsToOther(i+2, game))
						{
							return i;
						}
					}
				}
			}
		}
		
		return -1; //enemy wasn't found near max
	}
	
	public boolean einaiSeMegaluterhThsDiplhs(Vector<Integer> posageitniazoun, Vector<Integer> geitonies, Vector<Integer> exeimonossegeitonia, FacilityGameAPI game)
	{
		
		for(int i=0; i<geitonies.size(); i++)
		{
			for(int j=0; j<exeimonossegeitonia.size(); j++)
			{
				if(geitonies.elementAt(i).equals(exeimonossegeitonia.elementAt(j)))
				{
					if(posageitniazoun.elementAt(i)>2) return true;
				}
			}
		}
		
		return false;
	}
	
	public int nextMove(FacilityGameAPI game)    
	{
		int move=-1;             
		int n = game.getN();
		int currentmax=0;
		int numberofmaxgeitonies=0;
		int maxneighborhood;
		
		Vector<Integer> maxpositions = new Vector<Integer>();
		Vector<Integer> mymultiples = new Vector<Integer>();          //vector with the first positions from which continuities start
		Vector<Integer> othermultiples = new Vector<Integer>();       ////////////////////////////////////////////////////////////////////////
		Vector<Integer> profitofmymultbestchoice =new Vector<Integer>();
		Vector<Integer> profitofothermultbestchoice =new Vector<Integer>();
		Vector<Integer> sumofmymultiples = new Vector<Integer>();     //vector with the sum of every continuity before the bonus
		Vector<Integer> sumofothermultiples = new Vector<Integer>();  ////////////////////////////////////////////////////////////////////////	
		Vector<Integer> mymultbestchoice = new Vector<Integer>();	  //vector with the bigger node left or right from continuities
		Vector<Integer> othermultbestchoice = new Vector<Integer>();  ////////////////////////////////////////////////////////////////////////
		Vector<Integer> myduals = new Vector<Integer>();	     	  //vector with the first positions from which doubles start
		Vector<Integer> otherduals = new Vector<Integer>();		      ///////////////////////////////////////////////////////////////////////////
		Vector<Integer> profitofmydualswithbestchoice = new Vector<Integer>();       //gain from forming a triad including the bonus
		Vector<Integer> profitofotherdualswithbestchoice = new Vector<Integer>();    ////////////////////////////////////////////////////////////////////
		Vector<Integer> mydualsbestchoice = new Vector<Integer>();    //vector with the bigger node left or right from doubles
		Vector<Integer> otherdualsbestchoice = new Vector<Integer>(); //////////////////////////////////////////////////////////////////////////
		Vector<Integer> geitonies = new Vector<Integer>();			  //neighborhoods
		Vector<Integer> geitoniessum = new Vector<Integer>();		  //the hypotheitcal better sum of every neighborhood if i catch all the nodes, before the bonus
		Vector<Integer> posageitniazoun = new Vector<Integer>();	  //here another vector is not put, as we are talking about max numbers and so bigger neighborhoods will have bigger sums 
		Vector<Integer> maxgeitonies = new Vector<Integer>();		  //the positions from which max neighborhoods start
		Vector<Integer> kanonik = new Vector<Integer>();			  //the indexes in the max neighborhoods vector
		Vector<Integer> endofgeitonies = new Vector<Integer>();
		Vector<Integer> exwmonossegeitonia = new Vector<Integer>();
		Vector<Integer> exeimonossegeitonia = new Vector<Integer>();
		Vector<Integer> improvedmykotsarisma = new Vector<Integer>();
		Vector<Integer> kerdosofmykotsarisma = new Vector<Integer>();
		Vector<Integer> improvedotherkotsarisma = new Vector<Integer>();   
		Vector<Integer> kerdosofotherkotsarisma = new Vector<Integer>();   
		Vector<Integer> higherpointsicanget = new Vector<Integer>();
		Vector<Integer> positionofhigherpointsicanget = new Vector<Integer>();
		Vector<Integer> higherpointshecanget = new Vector<Integer>();
		Vector<Integer> positionofhigherpointshecanget = new Vector<Integer>();

		 curMoveIndex = game.getCurMoveIndex(); 
		////////////////////////////////////////////////////////////////////////////////////////////////////	
		//find free maxes and give max
		
		currentmax=acquireMaxPositions(maxpositions, game);
		 
		////////////////////////////////////////////////////////////////////////////////////////////////////
		//find continuities and solos
		
		findContinuous(profitofothermultbestchoice, profitofmymultbestchoice, mymultiples, othermultiples, sumofmymultiples, sumofothermultiples, mymultbestchoice, othermultbestchoice, myduals, otherduals, profitofmydualswithbestchoice, profitofotherdualswithbestchoice, mydualsbestchoice, otherdualsbestchoice, game);
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		//find where to plug moves
		
		
		findMyKotsarismata(improvedmykotsarisma, kerdosofmykotsarisma, game);
		findOtherKotsarismata(improvedotherkotsarisma, kerdosofotherkotsarisma, game);
		////////////////////////////////////////////////////////////////////////////////////////////////////
		//which move will bring the most poitns to me and to the enemy
		
		myMoveForMaxpoints(higherpointshecanget, improvedmykotsarisma, mymultbestchoice, mydualsbestchoice, kerdosofmykotsarisma, profitofmymultbestchoice, profitofmydualswithbestchoice, higherpointsicanget, positionofhigherpointsicanget, maxpositions, game);
		hisMoveForMaxPoints(improvedotherkotsarisma, profitofothermultbestchoice, otherdualsbestchoice, kerdosofotherkotsarisma, othermultbestchoice, profitofotherdualswithbestchoice, higherpointshecanget, positionofhigherpointshecanget);
		////////////////////////////////////////////////////////////////////////////////////////////////////
		//find neighborhoods
		
		findNeighborhoods(geitonies, endofgeitonies, posageitniazoun, geitoniessum, exeimonossegeitonia, exwmonossegeitonia, currentmax, game);	
		maxneighborhood=findMaxNeighborhood(posageitniazoun);	
		numberofmaxgeitonies=findHowManyMaxNeighborhoods(posageitniazoun, maxneighborhood, kanonik, maxgeitonies, geitonies);
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		//select what to play
		
		if(n>150)
		{
			if(doIHaveContiniousOrKotsarismataOrDuals(mydualsbestchoice, mymultbestchoice, improvedmykotsarisma)==true)
			{
				if(doesHeHaveContiniousOrKotsarismataOrDuals(otherdualsbestchoice, othermultbestchoice, improvedotherkotsarisma))
				{
					if(higherpointsicanget.firstElement()-higherpointshecanget.firstElement()>=0)
					{
						if(einaiToSunexomenoKotsarismaMegaluteroApoToSuntelesthPouExwPei(higherpointshecanget, improvedmykotsarisma, mymultbestchoice, mydualsbestchoice, kerdosofmykotsarisma, profitofmymultbestchoice, profitofmydualswithbestchoice, higherpointsicanget, positionofhigherpointsicanget, maxpositions, game)==true)
						{
							move=positionofhigherpointsicanget.firstElement();
						}
						else
						{
							if(geitonies.isEmpty()==false)
							{
								move=chooseInMaxNeighborhood(maxpositions, maxneighborhood, geitoniessum, geitonies, endofgeitonies, maxgeitonies, kanonik, posageitniazoun, numberofmaxgeitonies, currentmax, game);
							}
							else
							{
								move=positionofhigherpointsicanget.firstElement();
							}
						}
					}
					else
					{
						if(aksizeiNaTonBlockarw(n, currentmax, improvedotherkotsarisma, profitofothermultbestchoice, otherdualsbestchoice, kerdosofotherkotsarisma, othermultbestchoice, profitofotherdualswithbestchoice, higherpointshecanget, positionofhigherpointshecanget)==true)
						{
							move=positionofhigherpointshecanget.firstElement();
						}
						else
						{
							if(geitonies.isEmpty()==false)
							{
								move=chooseInMaxNeighborhood(maxpositions, maxneighborhood, geitoniessum, geitonies, endofgeitonies, maxgeitonies, kanonik, posageitniazoun, numberofmaxgeitonies, currentmax, game);
							}
							else
							{
								move=positionofhigherpointsicanget.firstElement();
							}
						}
					}
				}
				else
				{
					if(geitonies.isEmpty()==false)
					{
						if(einaiToSunexomenoKotsarismaMegaluteroApoToSuntelesthPouExwPei(higherpointshecanget, improvedmykotsarisma, mymultbestchoice, mydualsbestchoice, kerdosofmykotsarisma, profitofmymultbestchoice, profitofmydualswithbestchoice, higherpointsicanget, positionofhigherpointsicanget, maxpositions, game)==true)
						{
							move=positionofhigherpointsicanget.firstElement();
						}
						else
						{
							move=chooseInMaxNeighborhood(maxpositions, maxneighborhood, geitoniessum, geitonies, endofgeitonies, maxgeitonies, kanonik, posageitniazoun, numberofmaxgeitonies, currentmax, game);
						}
					}
					else
					{
						move=positionofhigherpointsicanget.firstElement();			
					}
				}
			}
			else
			{
				if(doesHeHaveContiniousOrKotsarismataOrDuals(otherdualsbestchoice, othermultbestchoice, improvedotherkotsarisma))
				{
					if(aksizeiNaTonBlockarw(n, currentmax, improvedotherkotsarisma, profitofothermultbestchoice, otherdualsbestchoice, kerdosofotherkotsarisma, othermultbestchoice, profitofotherdualswithbestchoice, higherpointshecanget, positionofhigherpointshecanget)==true)
					{
						move=positionofhigherpointshecanget.firstElement();
					}
					else
					{
						if(geitonies.isEmpty()==false)
						{
							move=chooseInMaxNeighborhood(maxpositions, maxneighborhood, geitoniessum, geitonies, endofgeitonies, maxgeitonies, kanonik, posageitniazoun, numberofmaxgeitonies, currentmax, game);
						}
						else
						{
							move=positionofhigherpointshecanget.firstElement();
						}
					}
				}
				else
				{
					if(geitonies.isEmpty()==false)
					{
						if(exeimonossegeitonia.isEmpty()==false)
						{
							if(einaiSeMegaluterhThsDiplhs(posageitniazoun, geitonies, exeimonossegeitonia, game)==true)
							{
								if(exwmonossegeitonia.isEmpty()==false)
								{
									move=chooseInMaxNeighborhood(maxpositions, maxneighborhood, geitoniessum, geitonies, endofgeitonies, maxgeitonies, kanonik, posageitniazoun, numberofmaxgeitonies, currentmax, game);
								}
								else
								{
									move=blockHimWhenHeIsAlone(currentmax, exeimonossegeitonia, geitonies, posageitniazoun, endofgeitonies, game);
								}
							}
							else
							{
								move=chooseInMaxNeighborhood(maxpositions, maxneighborhood, geitoniessum, geitonies, endofgeitonies, maxgeitonies, kanonik, posageitniazoun, numberofmaxgeitonies, currentmax, game);
							}
						}
						else
						{
							move=chooseInMaxNeighborhood(maxpositions, maxneighborhood, geitoniessum, geitonies, endofgeitonies, maxgeitonies, kanonik, posageitniazoun, numberofmaxgeitonies, currentmax, game);
						}
					}
					else
					{
						move=positionofhigherpointsicanget.firstElement();
					}
				}
			}
		}
		else   //auto gia tous 40
		{
			if(doesHeHaveContiniousOrKotsarismataOrDuals(otherdualsbestchoice, othermultbestchoice, improvedotherkotsarisma))
			{
				if(higherpointsicanget.firstElement()-higherpointshecanget.firstElement()>=0)
				{
					move=positionofhigherpointsicanget.firstElement();
				}
				else
				{		
					if(aksizeiNaTonBlockarw(n, currentmax, improvedotherkotsarisma, profitofothermultbestchoice, otherdualsbestchoice, kerdosofotherkotsarisma, othermultbestchoice, profitofotherdualswithbestchoice, higherpointshecanget, positionofhigherpointshecanget)==true)
					{
						move=positionofhigherpointshecanget.firstElement();
					}
					else
					{
						if(geitonies.isEmpty()==false)
						{
							move=chooseInMaxNeighborhood(maxpositions, maxneighborhood, geitoniessum, geitonies, endofgeitonies, maxgeitonies, kanonik, posageitniazoun, numberofmaxgeitonies, currentmax, game);
						}
						else
						{
							move=positionofhigherpointsicanget.firstElement();
						}
					}
				}
			}
			else
			{
				if(doIHaveContiniousOrKotsarismataOrDuals(mydualsbestchoice, mymultbestchoice, improvedmykotsarisma)==true)
				{
					move=positionofhigherpointsicanget.firstElement();
				}
				else
				{
					if(geitonies.isEmpty()==false)
					{
						move=chooseInMaxNeighborhood(maxpositions, maxneighborhood, geitoniessum, geitonies, endofgeitonies, maxgeitonies, kanonik, posageitniazoun, numberofmaxgeitonies, currentmax, game);
					}
					else
					{
						move=positionofhigherpointsicanget.firstElement();
					}
			    	if(move==-1)
			    	{
			    		move=takeTheOneThatHasTheBiggerNumberNear(maxpositions, game);
			    		if(move==-1)
			    		{
			    			move=maxpositions.elementAt(0);
			    		}
			    	}	    
				}
			}
		}
		////////////////////////////////////////////////////////////////////////////////////////////////////
			
		return move;
	}
}