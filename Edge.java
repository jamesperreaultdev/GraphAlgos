package cs1501_p4;

public class Edge {

    public int VertexTo;
    public int VertexFrom; //only used for primms 
    public String type;
    public int bandwidth;
    public Edge nextEdge;
    public int length;

    public Edge(int Vertex, String cable, int speed, int length) {
        VertexTo = Vertex;
        type = cable;
        bandwidth = speed;
        this.length = length;
    }

}
