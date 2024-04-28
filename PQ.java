package cs1501_p4;

import java.util.NoSuchElementException;



public class PQ {

    public Edge[] pq;                    // store items at indices 1 to n
    private int n;                       // number of items on priority queue
   

    /**
     * Initializes an empty priority queue with the given initial capacity.
     *
     * @param  initCapacity the initial capacity of this priority queue
     */
    public PQ(int initCapacity) {
        pq =  new Edge[initCapacity+1];
        n = 0;
    }

    /**
     * Initializes an empty priority queue.
     */
    public PQ() {
        this(1);
    }


    /**
     * Returns true if this priority queue is empty.
     *
     * @return {@code true} if this priority queue is empty;
     *         {@code false} otherwise
     */
    public boolean isEmpty() {
        return n == 0;
    }

    /**
     * Returns the number of keys on this priority queue.
     *
     * @return the number of keys on this priority queue
     */
    public int size() {
        return n;
    
    }
 
    /**
     * Returns a smallest key on this priority queue.
     *
     * @return a smallest key on this priority queue
     * @throws NoSuchElementException if this priority queue is empty
     */
    public Edge min() {
        if (isEmpty()) throw new NoSuchElementException("Priority queue underflow");
        return pq[1];
    }

    // resize the underlying array to have the given capacity
    private void resize(int capacity) {
        assert capacity > n;
        Edge[] temp = new Edge[capacity];
        for (int i = 1; i <= n; i++) {
            temp[i] = pq[i];
        }
        pq = temp;
    }

    /**
     * Adds a new key to this priority queue.
     *
     * @param  x the key to add to this priority queue
     */
    public void insert(Edge x) {
        // double size of array if necessary
        if (n == pq.length - 1) resize(2 * pq.length);

        // add x, and percolate it up to maintain heap invariant
        
        pq[++n] = x;
        swim(n);
        assert isMinHeap();
    }

    /**
     * Removes and returns a smallest key on this priority queue.
     *
     * @return a smallest key on this priority queue
     * @throws NoSuchElementException if this priority queue is empty
     */
    public Edge delMin() {
        if (isEmpty()) throw new NoSuchElementException("Priority queue underflow");
        Edge min = pq[1];
        exch(1, n--);
        sink(1);
        pq[n+1] = null;     // to avoid loitering and help with garbage collection
        if ((n > 0) && (n == (pq.length - 1) / 4)) resize(pq.length / 2);
        assert isMinHeap();
        return min;
    }

    public void remove(int index){
        //replace index with last node
        exch(index, size());


        //replace last node wiht null
        
        pq[size()] = null;
        
        //maybe this works
        n--;

        sink(index);
        swim(index);
    }


   /***************************************************************************
    * Helper functions to restore the heap invariant.
    ***************************************************************************/

    private void swim(int k) {
        while (k > 1 && greater(k/2, k)) {
            exch(k/2, k);
            k = k/2;
        }
    }

    private void sink(int k) {
        while (2*k <= n) {
            int j = 2*k;
            if (j < n && greater(j, j+1)) j++;
            if (!greater(k, j)) break;
            exch(k, j);
            k = j;
        }
    }

   /***************************************************************************
    * Helper functions for compares and swaps.
    ***************************************************************************/
    private boolean greater(int i, int j) {
 
        if(latency(pq[i]) > latency(pq[j])){
            return true;
        }

        return false;

        
    }

    private float latency(Edge edge){
        if(edge.type.equals("optical")){
            return(((float) edge.length / 200000000));
        }
        else{
            return(((float) edge.length / 230000000));
        }

    }

    private void exch(int i, int j) {
        Edge swap = pq[i];
        
        //change reference here
        pq[i] = pq[j];
        pq[j] = swap;
    }

    // is pq[1..n] a min heap?
    private boolean isMinHeap() {
        for (int i = 1; i <= n; i++) {
            if (pq[i] == null) return false;
        }
        for (int i = n+1; i < pq.length; i++) {
            if (pq[i] != null) return false;
        }
        if (pq[0] != null) return false;
        return isMinHeapOrdered(1);
    }

    // is subtree of pq[1..n] rooted at k a min heap?
    private boolean isMinHeapOrdered(int k) {
        if (k > n) return true;
        int left = 2*k;
        int right = 2*k + 1;
        if (left  <= n && greater(k, left))  return false;
        if (right <= n && greater(k, right)) return false;
        return isMinHeapOrdered(left) && isMinHeapOrdered(right);
    }


   

    }