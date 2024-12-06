package nodes;

import java.util.HashMap;

public class EdgeNode extends BasicNode{
        
		public EdgeNode(int id,double alpha,HashMap<String, Long> eventLog,String algorithmName) {
			super(id, alpha, eventLog, algorithmName); 
		}
        /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
        public EdgeNode(int id,double alpha,String fileName,String algorithmName) {
        	super(id, alpha, fileName, algorithmName); 
        }
        /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/       
        public EdgeNode(double alpha,int num,String algorithmName) {
        	super(alpha, num, algorithmName);
        }
        /*-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+*/
     

}