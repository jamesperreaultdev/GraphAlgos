package cs1501_p4;

import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class NetAnalysis {

    int vertexSize = 0;
    ArrayList<Edge> VertexGraph = new ArrayList<Edge>();

    NetAnalysis(String filename) {

        try {
            Scanner scan = new Scanner(new File(filename));
            int vertexLength = 0;

            // scan 1st line
            String line;
            if (scan.hasNextLine()) {
                line = scan.nextLine();
                vertexLength = Integer.parseInt(line);
            }

            vertexSize = vertexLength;
            // add nulls
            for (int i = 0; i < vertexLength; i++) {
                VertexGraph.add(null);
            }

            // scanner
            while (scan.hasNextLine()) {

                line = scan.nextLine();
                String[] lineSplit = line.split("\\s+");
                Edge test = VertexGraph.get(Integer.parseInt(lineSplit[0]));

                Edge test2 = VertexGraph.get(Integer.parseInt(lineSplit[1]));

                // make edge
                Edge newEdge = new Edge(Integer.parseInt(lineSplit[1]), lineSplit[2], Integer.parseInt(lineSplit[3]),
                        Integer.parseInt(lineSplit[4]));
                Edge backEdge = new Edge(Integer.parseInt(lineSplit[0]), lineSplit[2], Integer.parseInt(lineSplit[3]),
                        Integer.parseInt(lineSplit[4]));

                // get the vertex and go the last linked node of the list, if it exists
                if (test == null) {
                    VertexGraph.set(Integer.parseInt(lineSplit[0]), newEdge);

                }
                // get the vertex and go the last linked node of the list
                else {
                    while (test.nextEdge != null) {
                        test = test.nextEdge;
                    }
                    test.nextEdge = newEdge;
                }

                if (test2 == null) {
                    VertexGraph.set(Integer.parseInt(lineSplit[1]), backEdge);

                }
                // get the vertex and go the last linked node of the list
                else {
                    while (test2.nextEdge != null) {
                        test2 = test2.nextEdge;
                    }
                    test2.nextEdge = backEdge;
                }

            }
            scan.close();
        } catch (FileNotFoundException e) {
            VertexGraph = null;
        }

    }

    /**
     * Find the lowest latency path from vertex `u` to vertex `w` in the graph
     *
     * @param u Starting vertex
     * @param w Destination vertex
     *
     * @return ArrayList<Integer> A list of the vertex id's representing the
     *         path (should start with `u` and end with `w`)
     *         Return `null` if no path exists
     */
    public ArrayList<Integer> lowestLatencyPath(int u, int w) {
        ArrayList<Integer> solution = new ArrayList<Integer>();

        float[] distanceArray = new float[vertexSize];
        Integer[] viaArray = new Integer[vertexSize];
        boolean[] visited = new boolean[vertexSize];

        for (int i = 0; i < distanceArray.length; i++) {
            distanceArray[i] = Integer.MAX_VALUE;
        }

        for (int i = 0; i < viaArray.length; i++) {
            viaArray[i] = null;
        }


        int cur = u;

        // for calculating next best

        // get starts neighbors
        Edge currentNeighbor = VertexGraph.get(cur);
       
        // continue to do so
        while (currentNeighbor != null) {

            if (currentNeighbor.type.equals("optical")) {

                distanceArray[currentNeighbor.VertexTo] = ((float) currentNeighbor.length / 200000000);
                viaArray[currentNeighbor.VertexTo] = cur;

            } else {

                distanceArray[currentNeighbor.VertexTo] = ((float) currentNeighbor.length / 230000000);
                viaArray[currentNeighbor.VertexTo] = cur;
            }

            currentNeighbor = currentNeighbor.nextEdge;
        }

        int next = -1;
        float nextDistance = Integer.MAX_VALUE;
        visited[cur] = true;

        for (int i = 0; i < visited.length; i++) {
            if ((visited[i] != true)) {
                if (distanceArray[i] < nextDistance) {
                    nextDistance = distanceArray[i];
                    next = i;
                }
            }
        }

        if (next == -1) {
            return null;
        }

      
        cur = next;

        while (cur != w) {
          
            currentNeighbor = VertexGraph.get(cur);

            float edgeLatency = 0;

            if (currentNeighbor.type.equals("optical")) {
                edgeLatency = ((float) currentNeighbor.length / 200000000);
            } else {
                edgeLatency = ((float) currentNeighbor.length / 230000000);
            }

            if ((distanceArray[cur] + edgeLatency) < distanceArray[currentNeighbor.VertexTo]) {
                distanceArray[currentNeighbor.VertexTo] = distanceArray[cur] + edgeLatency;
                viaArray[currentNeighbor.VertexTo] = cur;
            }

            currentNeighbor = currentNeighbor.nextEdge;

            while (currentNeighbor != null) {

                edgeLatency = 0;

                if (currentNeighbor.type.equals("optical")) {
                    edgeLatency = ((float) currentNeighbor.length / 230000000);

                } else {
                    edgeLatency = ((float) currentNeighbor.length / 230000000);
                }

                if ((distanceArray[cur] + edgeLatency) < distanceArray[currentNeighbor.VertexTo]) {

                    distanceArray[currentNeighbor.VertexTo] = distanceArray[cur] + edgeLatency;
                    viaArray[currentNeighbor.VertexTo] = cur;
                }

                currentNeighbor = currentNeighbor.nextEdge;

            }

            next = -1;
            nextDistance = Integer.MAX_VALUE;
            visited[cur] = true;

   
            for (int i = 0; i < visited.length; i++) {
                if (visited[i] != true) {
                    if (distanceArray[i] < nextDistance) {
                        
                        nextDistance = distanceArray[i];
                        next = i;
                    }
                }
            }

            if (next == -1)
                return null;

            cur = next;
        }

        // continue to do so
        int x = cur;
        solution.add(w);

     


        while ((viaArray[x] != u)) {
            solution.add(0, viaArray[x]);
            x = viaArray[x];
        }

        solution.add(0, u);

  
     
        return solution;
    }

    /**
     * Find the bandwidth available along a given path through the graph
     * (the minimum bandwidth of any edge in the path). Should throw an
     * `IllegalArgumentException` if the specified path is not valid for
     * the graph.
     *
     * @param ArrayList<Integer> A list of the vertex id's representing the
     *                           path
     *
     * @return int The bandwidth available along the specified path
     */
    public int bandwidthAlongPath(ArrayList<Integer> p) throws IllegalArgumentException {

        if(p.size() == 0 || p.size() == 1){
            throw new IllegalArgumentException();
        }
        int bandwidth = Integer.MAX_VALUE;
        ArrayList<Integer> bandwithPath = p;

        for (int i = 0; i < bandwithPath.size()-1; i++) {
            int vertex = bandwithPath.get(i);
            int vertexNext = bandwithPath.get(i+1);
            
            Edge edgecheck = VertexGraph.get(vertex);


            while(edgecheck.VertexTo != vertexNext)
                edgecheck = edgecheck.nextEdge;

          

            if (edgecheck.bandwidth < bandwidth) {
                bandwidth = edgecheck.bandwidth;
            }

            edgecheck = edgecheck.nextEdge;


        }

        return bandwidth;
    }

    /**
     * Return `true` if the graph is connected considering only copper links
     * `false` otherwise
     *
     * @return boolean Whether the graph is copper-only connected
     */
    public boolean copperOnlyConnected() {

        boolean[] visited = new boolean[vertexSize];

        Edge check = VertexGraph.get(0);

        visited[0] = true;
        while (check != null) {
            if (check.type.equals("copper")) {

                visited = copperHelper(check.VertexTo, visited);
            }
            check = check.nextEdge;
        }

        for (int i = 0; i < visited.length; i++) {
            if (visited[i] == false) {

                return false;
            }
        }
        return true;
    }

    private boolean[] copperHelper(int vertex, boolean[] visited) {

        Edge check = VertexGraph.get(vertex);

        visited[vertex] = true;
        while (check != null) {
            if (check.type.equals("copper") && (visited[check.VertexTo] == false)) {

                visited = copperHelper(check.VertexTo, visited);
            }
            check = check.nextEdge;
        }
        return visited;
    }

    /**
     * Return `true` if the graph would remain connected if any two vertices in
     * the graph would fail, `false` otherwise
     *
     * @return boolean Whether the graph would remain connected for any two
     *         failed vertices
     * 
     * 
     */

     int count;
     int AP;
    public boolean connectedTwoVertFail() {
        boolean[] visited = new boolean[vertexSize];
        int[] num = new int[vertexSize];
        int[] low = new int[vertexSize];

        AP = 0;
        
        count = 0;

        if(vertexSize < 3){
            return false;
        }
        
         //tri-connected test
        for(int i = 0;i<vertexSize;i++){
            
            for (int root = 0; root < vertexSize; root++){
                if (visited[root] == false)
                    vertHelper(root, visited, num, low, -1,i);
                if(AP > 0){
                  return false;
                }
            }
            if(AP > 0){
                return false;
            }

            visited = new boolean[vertexSize];
            num = new int[vertexSize];
            low = new int[vertexSize];
            count = 0;

        }

        
        if(AP > 0){
           
            return false;
        }
        
        System.out.println(AP);
        return true;
    }


    private void vertHelper(int u, boolean visited[], int num[], int low[],int parent, int test)
    {
       
        
        visited[u] = true;

        if(u == test){
            return;
        }
        num[u] = count;
        low[u] = count;
        count++;
        int children = 0;
       
        
        Edge iterEdge = VertexGraph.get(u);
        while(iterEdge !=null) {
            
            if (!visited[iterEdge.VertexTo] && iterEdge.VertexTo != test ) {
                //add bad number check
                children++;
                vertHelper(iterEdge.VertexTo, visited, num, low, u,test);
                
                //min low
                low[u] = Math.min(low[u], low[iterEdge.VertexTo]);
 
                if (parent != -1 && low[iterEdge.VertexTo] >= num[u]){
                    AP++;
                    
                }
                  
            }

            else if (iterEdge.VertexTo != parent)
                low[u] = Math.min(low[u], num[iterEdge.VertexTo]);
           
            iterEdge = iterEdge.nextEdge;
        }
 
       //root check
        if (parent == -1 && children > 1){
            
            AP++;
           
        }
    }
 
   

    /**
     * Find the lowest average (mean) latency spanning tree for the graph
     * (i.e., a spanning tree with the lowest average latency per edge). Return
     * it as an ArrayList of STE edges.
     *
     * Note that you do not need to use the STE class to represent your graph
     * internally, you only need to use it to construct return values for this
     * method.
     *
     * @return ArrayList<STE> A list of STE objects representing the lowest
     *         average latency spanning tree
     *         Return `null` if the graph is not connected
     */
    ArrayList<STE> check = new ArrayList<STE>();
    PQ pq =  new PQ();
    boolean[] visited = new boolean[vertexSize];

    public ArrayList<STE> lowestAvgLatST() {
        check = new ArrayList<STE>();
        pq =  new PQ();
        visited = new boolean[vertexSize];

            for (int v = 0; v < vertexSize; v++)     // run Prim from all vertices to
                if (!visited[v]) prim(v);     // get a minimum spanning forest
            
            return check;
 
    }

       
       
    private void prim(int s) {
            scan(s);
            while (!pq.isEmpty()) {                        // better to stop when mst has V-1 edges
                Edge e = pq.delMin();                      // smallest edge on pq
                int v = e.VertexFrom, w = e.VertexTo;        // two endpoints
                
                if (visited[e.VertexFrom] && visited[e.VertexTo]) continue;      // lazy, both v and w already scanned
                check.add(new STE(e.VertexFrom,e.VertexTo));                       // add e to MST
                if (!visited[v]) scan(v);               // v becomes part of tree
                if (!visited[w]) scan(w);               // w becomes part of tree
            }
        }

    private void scan(int v) {
            
            visited[v] = true;
            Edge edge = VertexGraph.get(v);
            while(edge != null){
                if(!visited[edge.VertexTo]){
                    edge.VertexFrom = v;
                    pq.insert(edge);
                }
                edge = edge.nextEdge;
            }

        }

        
    }
    // find if path from neighbor is smaller than previous path in via array

