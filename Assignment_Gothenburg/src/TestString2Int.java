import java.util.*;

public class TestString2Int {

	public static void main(String[] args) {
		String temp = "-1 2 -3 -4 0";
		StringBuilder sb = new StringBuilder(5);
		int index = 0;
		char tempChar;
		int tempNum;
		LinkedList<Integer> list = new LinkedList<Integer>();

		boolean readEnd = false;
		while (!readEnd)
		{
			tempChar = temp.charAt(index);
				switch (tempChar) {
				case '-': 
					sb.append('-');
				break;
				case ' ':
					tempNum= Integer.parseInt(sb.toString()); 
					sb.setLength(0);
					// save num if < 0
					if(tempNum < 0)
					{
						 list.offer(tempNum);
					}
						
					
				break;
				case '0':
					readEnd = true;
				break;
				default : 
					sb.append(tempChar);
				break;
				}
					
					index = index +1;
		}
		while (list.size()>0)
		{
			System.out.println(list.poll());
		}

	}

}
