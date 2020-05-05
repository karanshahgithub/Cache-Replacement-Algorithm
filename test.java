/* Version 8 */

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class test{

    public static void main(String[ ] args){
        //randomNumberGenerator();  // comparing two mechanism generating random numbers
        repetitiveNumberGenerator(); // comparing two mechanism generating repetitive numbers
    }

    private static void randomNumberGenerator(){
        int maxCapacity = 3;
        int maxInput = 50;

        FIFO f1 = new FIFO(maxCapacity);
        LRUCache l1 = new LRUCache(maxCapacity);

        int min=1,max=5;
        int[] randomNum = new int[maxInput];
        for(int i=0;i<maxInput;i++){
            randomNum[i] = ThreadLocalRandom.current().nextInt(min, max + 1);
        }
        Optimal O1 = new Optimal(maxCapacity,randomNum,maxInput);

        // printing input
        System.out.print("--- input---");
        for(int i=0;i<maxInput;i++){
            System.out.print(randomNum[i]);
        }

        // calling  FIFO method
        System.out.print("\n ----- RUNNING FIFO ----");

        for(int i=0;i<maxInput;i++){
            f1.put(randomNum[i],randomNum[i]);
        }

        // calling  LRUCache method
        System.out.print("\n ----- RUNNING LRUCache ----");

        for(int i=0;i<maxInput;i++){
            l1.put(randomNum[i],randomNum[i]);
        }

        // calling Optimal method
        System.out.print("\n ----- RUNNING Optimal----");

        for(int i=0;i<maxInput;i++){
            O1.put(randomNum[i],randomNum[i]);
        }

        System.out.print("\n ---- FIFO Results -----");
        f1.rate();

        System.out.print("\n ---- LRUCache Results -----");
        l1.rate();

        System.out.print("\n ---- Optimal Results -----");
        O1.rate();
    }

    private static void repetitiveNumberGenerator(){

        int maxCapacity = 3;
        int maxInput = 11;

        FIFO f1 = new FIFO(maxCapacity);
        LRUCache l1 = new LRUCache(maxCapacity);
        Skiplist s1 = new Skiplist(maxCapacity);

        int[] randomNum = new int[]{1,2,3,1,4,1,5,1,6,7,1};

        Optimal O1 = new Optimal(maxCapacity,randomNum,maxInput);

        // printing input
        System.out.print("--- input---");
        for(int i=0;i<maxInput;i++){
            System.out.print(randomNum[i]);
        }


        // calling  FIFO method
        System.out.print("\n ----- RUNNING FIFO ----");

        for(int i=0;i<maxInput;i++){
            f1.put(randomNum[i],randomNum[i]);
        }

        // calling  LRUCache method
        System.out.print("\n ----- RUNNING LRUCache ----");

        for(int i=0;i<maxInput;i++){
            l1.put(randomNum[i],randomNum[i]);
        }

        // calling Optimal method
        System.out.print("\n ----- RUNNING Optimal----");

        for(int i=0;i<maxInput;i++){
            O1.put(randomNum[i],randomNum[i]);
        }

        // calling SkipList method
        System.out.print("\n ---- RUNNING SkipList----");

        for(int i=0;i<maxInput;i++){
            System.out.print("\n *****************" + randomNum[i]);
            s1.put(randomNum[i],randomNum[i]);
        }


        System.out.print("\n ---- FIFO Results -----");
        f1.rate();

        System.out.print("\n ---- LRUCache Results -----");
        l1.rate();


        System.out.print("\n ---- Optimal Results -----");
        O1.rate();

        System.out.print("\n ---- LFU Results -----");
        s1.rate();

    }


}

// FIFO Mechanism implementation

class FIFO {
    float hit = 0;
    float miss = 0;
    int maxcapacity;
    int currentcapacity=0;
    HashMap<Integer,listNode> keyToNode = new HashMap();
    listNode head;
    listNode tail;

    public FIFO(int capacity) {

        this.maxcapacity = capacity;
        head = new listNode();
        tail = new listNode();
        head.prev = null;
        tail.next = null;
        head.next = tail;
        tail.prev = head;

    }

    public void put(int key, int value) {
        Boolean temp = keyToNode.containsKey(key);

        if(temp){
            hit ++;
            listNode temp2 = keyToNode.get(key);

        }

        else{
            if(currentcapacity==maxcapacity){
                removeFromFront();
                currentcapacity--;
            }

            listNode temp2 = new listNode();
            temp2.key = key;
            temp2.value = value;
            miss ++;
            keyToNode.put(temp2.key,temp2);
            addToTail(temp2);

        }


        traverse();
    }



    private void addToTail(listNode temp){

        listNode tailPrev = tail.prev;
        tail.prev = temp;
        temp.next = tail;
        temp.prev = tailPrev;
        tailPrev.next = temp;

        currentcapacity++;
        if(currentcapacity>maxcapacity){
            removeFromFront();
            currentcapacity--;
        }


    }

    private void removeFromFront(){
        listNode temp = head.next;
        listNode temp2 = temp.next;
        head.next = temp2;
        keyToNode.remove(temp.key);
    }

    private void traverse(){
        listNode temp = head.next;
        System.out.print('\n');
        System.out.print(temp.value);
        temp = temp.next;
        while(temp!=tail){
            System.out.print("-->");
            System.out.print(temp.value);
            temp = temp.next;
        }
    }

    public void rate(){
        System.out.print(" \n hit ratio is ");
        System.out.print(hit/(hit+miss));
        System.out.print(" \n miss ratio is ");
        System.out.print(miss/(hit+miss));
    }

    private class listNode{

        int key;
        int value;
        listNode next;
        listNode prev;


    }

}

// LRUCache mechanism implementation

class LRUCache {
    int maxcapacity;
    int currentcapacity=0;
    HashMap<Integer,listNode> keyToNode = new HashMap();
    listNode head;
    listNode tail;
    float hit = 0;
    float miss = 0;
    public LRUCache(int capacity) {

        this.maxcapacity = capacity;
        head = new listNode();
        tail = new listNode();
        head.prev = null;
        tail.next = null;
        head.next = tail;
        tail.prev = head;

    }

    public void put(int key, int value) {
        Boolean temp = keyToNode.containsKey(key);

        if(temp){
            hit++;
            listNode temp2 = keyToNode.get(key);
            temp2.value = value;
            moveToFront(temp2);
            addToFront(temp2);

        }

        else{
            if(currentcapacity==maxcapacity){
                removeFromEnd();
                currentcapacity--;
            }

            miss++;
            listNode temp2 = new listNode();
            temp2.key = key;
            temp2.value = value;
            keyToNode.put(temp2.key,temp2);
            addToFront(temp2);
            currentcapacity++;

        }



        traverse();
    }

    private void moveToFront(listNode temp){
        listNode savedPrev;
        listNode savedNext;
        savedPrev = temp.prev;
        savedNext = temp.next;
        savedPrev.next = savedNext;
        savedNext.prev =  savedPrev;

    }

    private void addToFront(listNode temp){

        listNode headNext = head.next;
        head.next = temp;
        temp.prev = head;
        temp.next = headNext;
        headNext.prev = temp;

    }

    private void removeFromEnd(){
        listNode temp = tail.prev;
        listNode temp2 = temp.prev;
        temp2.next = tail;
        tail.prev = temp2;
        keyToNode.remove(temp.key);
    }

    private void traverse(){
        listNode temp = head.next;
        System.out.print('\n');
        System.out.print(temp.value);
        temp = temp.next;
        while(temp!=tail){
            System.out.print("-->");
            System.out.print(temp.value);
            temp = temp.next;
        }
    }

    public void rate(){
        System.out.print(" \n hit ratio is ");
        System.out.print(hit/(hit+miss));
        System.out.print(" \n miss ratio is ");
        System.out.print(miss/(hit+miss));
    }

    private class listNode{

        int key;
        int value;
        listNode next;
        listNode prev;

    }

}

// optimal implementation
class Optimal {
    int maxcapacity;
    int currentcapacity=0;
    HashMap<Integer,listNode> keyToNode = new HashMap();
    listNode head;
    listNode tail;
    float hit = 0;
    float miss = 0;
    int[] randomNum = new int[1000];
    int maxInput;
    int currentPosition=0;

    public Optimal(int capacity,int[] randomNum,int maxInput) {
        this.randomNum = randomNum;
        this.maxInput = maxInput;
        this.maxcapacity = capacity;
        head = new listNode();
        tail = new listNode();
        head.prev = null;
        tail.next = null;
        head.next = tail;
        tail.prev = head;

    }

    public void put(int key, int value) {
        Boolean temp = keyToNode.containsKey(key);
        currentPosition++;

        if(temp){
            hit++;
            listNode temp2 = keyToNode.get(key);
        }


        else{

            if(currentcapacity==maxcapacity){
                removeFromCache();
                currentcapacity--;
            }

            miss++;
            listNode temp2 = new listNode();
            temp2.key = key;
            temp2.value = value;
            keyToNode.put(temp2.key,temp2);
            addToFront(temp2);
            currentcapacity++;

        }

        traverse();
    }


    private void addToFront(listNode temp){

        listNode headNext = head.next;
        head.next = temp;
        temp.prev = head;
        temp.next = headNext;
        headNext.prev = temp;

    }


    private void removeFromCache(){
        listNode temp = head.next;
        int farthest_key = temp.key;
        int farthest_position = 0;
        int count = 1;

        while(temp!=tail)
        {
            boolean check = false;
            for(int j=currentPosition;count<maxInput;j++){
                //System.out.print(randomNum[j]);
                if(j==(maxInput-1))
                    break;

                if(randomNum[j]==temp.key)
                {
                    if(j>farthest_position){
                        farthest_position =j;
                        farthest_key = temp.key;
                    }
                    temp = temp.next;
                    check = true;
                    count++;
                    continue;
                }

                //System.out.print(farthest_position);
                //System.out.print(farthest_key);
            }

            if(!check){
                farthest_key = temp.key;
                break;
            }

        }

        temp = keyToNode.get(farthest_key);

        listNode temp2 = temp.next;
        listNode temp3 = temp.prev;
        temp3.next = temp2;
        temp2.prev = temp3;
        keyToNode.remove(farthest_key);




    }

    private void traverse(){
        listNode temp = head.next;
        System.out.print('\n');
        System.out.print(temp.value);
        temp = temp.next;
        while(temp!=tail){
            System.out.print("-->");
            System.out.print(temp.value);
            temp = temp.next;
        }
    }

    public void rate(){
        System.out.print(" \n hit ratio is ");
        System.out.print(hit/(hit+miss));
        System.out.print(" \n miss ratio is ");
        System.out.print(miss/(hit+miss));
    }

    private class listNode{

        int key;
        int value;
        listNode next;
        listNode prev;

    }

}


// LRU with skip list
class Skiplist {
    int maxCapacity;
    int currentCapacity=0;

    listNode h1 = new listNode();
    listNode h2 = new listNode();
    listNode h3 = new listNode();
    listNode h4 = new listNode();
    listNode tail;
    float hit = 0;
    float miss = 0;
    public Skiplist(int capacity) {
        this.maxCapacity = capacity;
        /*
        h1.levelFrequency = 2;
        h1.up = h2;
        h1.down = null;
        h1.next = null;
        h2.levelFrequency = 4;
        h2.up = h3;
        h2.down = h1;
        h2.next = null;
        h3.levelFrequency = 6;
        h3.up = h4;
        h3.down = h2;
        h3.next = null;
        h4.levelFrequency = Integer.MAX_VALUE;
        h4.up = null;
        h4.down = h3;
        h4.next = null;
        */

        h1.frequency = 2;
        h2.frequency = 4;
        h3.frequency = 6;
        h4.frequency = Integer.MAX_VALUE;

        /*
        h1.key = null;
        h1.value = null;
        h2.key = null;
        h2.value = null;
        h3.key = null;
        h3.value = null;
        h4.key = null;
        h4.value = null;
        */
    }

    public void put(int key, int value) {

        boolean check = traverseLevel(key,value);
        //System.out.print(check);

        if(check){
            hit++;

        }

        else{
            //System.out.print("not found");

            miss++;
            if(currentCapacity==maxCapacity){
                //System.out.print("capacity exceeded");
                removeFromEnd();
                insertPage(key,value);
            }
            else{
                //System.out.print("not capacity exceeded");
                insertPage(key,value);
            }
        }

        traverse();
    }


    private boolean traverseLevel(int key,int value){
        //System.out.print("\n in traverse level");
        boolean check = false;
        if(!check){

            listNode temp = h4.next;
            while(temp!=null){
                if(temp.key==key){
                    //System.out.print("\n found level 4");
                    moveToFront(temp);
                    check = true;
                    break;
                }
                temp=temp.next;
            }
        }


        if(!check){

            listNode temp = h3.next;
            while(temp!=null){
                if(temp.key==key){
                    //System.out.print("\n found level 3");
                    moveToFront(temp);
                    check = true;
                    break;

                }
                temp=temp.next;

            }
        }

        if(!check){
            //System.out.print("if 3 ");
            listNode temp = h2.next;
            while(temp!=null){
                //System.out.print("\n found level 2");
                //System.out.println("*");
                if(temp.key==key){
                    moveToFront(temp);
                    check = true;
                    break;

                }
                temp=temp.next;

            }
        }

        if(!check){
            //System.out.print("if 4 ");
            listNode temp = h1.next;
            //System.out.print(h1.next);
            //System.out.print("in traversal");
            while(temp!=null){
                if(temp.key==key){
                    //System.out.print("found level 1");
                    //System.out.print("in traversal2");
                    moveToFront(temp);
                    check = true;
                    break;

                }
                temp=temp.next;

            }
        }
        //System.out.print(check);
        return check;

    }

    private void moveToFront(listNode temp){
        //System.out.print("\n WWWWWWWW");
        //System.out.print(h1.next);
        //System.out.print("in moveToFront !!!!");
        temp.frequency++;
        //System.out.print(temp.frequency);
        //System.out.print(h1.frequency);

        if(h1.frequency>temp.frequency){

            //System.out.print("in moveToFront 2");
            listNode temp2 = h1.next;
            if(temp!=temp2){
                listNode temp3 = temp.next;
                listNode temp4 = temp.prev;
                if(temp4!=null)
                    temp4.next = temp3;
                if(temp3!=null)
                    temp3.prev= temp4;

                h1.next = temp;
                temp.next = temp2;
                temp.prev = h1;
            }}

        else if(h2.frequency>temp.frequency){

            //System.out.print("in moveToFront 2");
            listNode temp2 = h2.next;
            if(temp!=temp2){
                listNode temp3 = temp.next;
                listNode temp4 = temp.prev;
                if(temp4!=null)
                    temp4.next = temp3;
                if(temp3!=null)
                    temp3.prev= temp4;

                h2.next = temp;
                temp.next = temp2;
                temp.prev = h2;
                if (temp2!=null)
                    temp2.prev=temp;
            }}

        else if(h3.frequency>temp.frequency){
            listNode temp2 = h3.next;
            if(temp!=temp2){
                listNode temp3 = temp.next;
                listNode temp4 = temp.prev;
                if(temp4!=null)
                    temp4.next = temp3;
                if(temp3!=null)
                    temp3.prev= temp4;

                h3.next = temp;
                temp.next = temp2;
                temp.prev = h3;
                if (temp2!=null)
                    temp2.prev=temp;

            }}

        else if(h4.frequency>temp.frequency){
            listNode temp2 = h4.next;
            if(temp!=temp2){
                listNode temp3 = temp.next;
                listNode temp4 = temp.prev;
                if(temp4!=null)
                    temp4.next = temp3;
                if(temp3!=null)
                    temp3.prev= temp4;

                h4.next = temp;
                temp.next = temp2;
                temp.prev = h4;
                if (temp2!=null)
                    temp2.prev=temp;

            }}


    }

    private void insertPage(int key,int value){
        //System.out.print("karan");
        listNode temp = new listNode();
        temp.key = key;
        temp.value = value;
        temp.frequency = 1;

        listNode temp2 = h1.next;
        //System.out.println("\n @@@@@@@@@@@");
        //System.out.println("\ntemp2"+temp2);
        //System.out.println("\ntemp"+temp);
        h1.next = temp;
        temp.next = temp2;
        temp.prev=h1;
        if(temp2!=null)
            temp2.prev = temp;
        currentCapacity++;
    }

    private void removeFromEnd(){

        if(h1.next!=null){
            listNode temp = h1.next;

            while(temp.next!=null){
                temp =temp.next;
            }

            temp.prev.next = null;
        }

        else if(h2.next!=null){
            listNode temp = h2.next;

            while(temp.next!=null){
                temp =temp.next;
            }

            temp.prev.next = null;
        }

        else if(h3.next!=null){
            listNode temp = h3.next;

            while(temp.next!=null){
                temp =temp.next;
            }

            temp.prev.next = null;
        }

        else if(h4.next!=null){
            listNode temp = h4.next;

            while(temp.next!=null){
                temp =temp.next;
            }

            temp.prev.next = null;
        }

        currentCapacity--;
    }


    private void traverse(){
        listNode temp = h4.next;
        //System.out.print(h4.next);
        System.out.print("\n frequency range greater than 6 --->");
        while(temp!=null){
            System.out.print("value-"+temp.key+" Frequency-"+temp.frequency);
            temp = temp.next;
        }


        temp = h3.next;
        //System.out.print(h3.next);
        System.out.print("\n frequency range 4 to 6 --->");
        while(temp!=null){
            System.out.print("value-"+temp.key+" Frequency-"+temp.frequency);
            temp = temp.next;
        }


        temp = h2.next;
        //System.out.print("!!!");
        //System.out.print("H2.next is"+temp.key);
        //System.out.print("H2.next is"+temp.key);
        /*if(temp!=null)
            System.out.println("\n temp.val "+temp.value);*/

        //System.out.print(h2.next);
        System.out.print("\n frequency range 2 to 4 --->");
        //int counter=1;
        while(temp!=null){
            System.out.print("value-"+temp.key+" Frequency-"+temp.frequency);
            temp = temp.next;
            //counter++;
        }
        //System.out.print("H2.next is"+h2.next.key);

        temp = h1.next;
        //System.out.print(h1.next);
        //System.out.print("H1.next is"+temp.key);
        System.out.print("\n frequency range  1 to 2 --->");
        while(temp!=null){
            System.out.print("value-"+temp.key+" Frequency-"+temp.frequency);
            temp = temp.next;
        }

    }

    public void rate(){
        System.out.print(" \n hit ratio is ");
        System.out.print(hit/(hit+miss));
        System.out.print(" \n miss ratio is ");
        System.out.print(miss/(hit+miss));
    }

    private class listNode{

        int key;
        int value;
        int frequency;
        listNode next;
        listNode prev;

    }

    private class head{

        int levelFrequency;
        head up;
        head down;
        listNode next;
    }

}




