import java.util.*;

/**
 * Created by Tian on 12/4/14.
 */

public class HuffmanTree {
    static class Node {
        public char character;
        public double frequency;
        public Node left;
        public Node right;
        public boolean visited;

        public Node(char character, double frequency, Node left, Node right, boolean visited) {
            this.character = character;
            this.frequency = frequency;
            this.left = left;
            this.right = right;
            this.visited = visited;
        }
        public boolean isLeaf() {
            return (right == null && left == null);
        }
    }
    /*****MAIN FUNCTION*****/
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter text to encode: ");
        String input = in.nextLine(); //input text

        char[] chars = input.toCharArray();
        List<Node> uniqueChars = uniqueChars(chars);

        LinkedList<Node> table = frequencyTable(uniqueChars);
        Node root = huffman(table);
        HashMap<Character, String> binaryTable = binaryTable(root, uniqueChars);
        String code = cleanString(encode(binaryTable, chars));

        System.out.println("\nInput text: " + input + "\nLength: " + chars.length + "\nBinary Code: " + code + "\nBits: " + code.toCharArray().length);
    }

    public static String cleanString(String code) {
        String cleanText = "";
        for (char c : code.toCharArray()) {
            if (c == '0') {
                cleanText = cleanText + "0";
            } else if (c == '1') {
                cleanText = cleanText + "1";
            }
        }
        return cleanText;
    }
    //Count the appearances of each unique char and record them. Returned list is stable.
    public static List<Node> uniqueChars(char[] chars) {
        HashMap<Character, Integer> uniqueChars = new HashMap<Character, Integer>();
        List<Character> stableTable = new ArrayList<Character>();
        for (char c : chars) {
            if (!uniqueChars.containsKey(c)) {
                uniqueChars.put(c, 1);
                stableTable.add(c);
            } else {
                uniqueChars.replace(c, uniqueChars.get(c), uniqueChars.get(c) + 1);
            }
        }
        List<Node> countTable = new ArrayList<Node>();
        for (int i = 0; i < stableTable.size(); i++) {
            char c = stableTable.get(i);
            countTable.add(new Node(c, uniqueChars.get(c), null, null, false));
        }
        /*****Standard English Frequencies*****/
        //String alphabet = "abcdefghijklmnopqrstuvwxyz";
        //char[] array = alphabet.toCharArray();
        //double[] frequency = {8.167, 1.492, 2.782, 4.253, 12.702, 2.228, 2.015, 6.094, 6.966, 0.153, 0.772, 4.025,
        //        2.406, 6.749, 7.507, 1.929, 0.095, 5.987, 6.327, 9.056, 2.758, 0.978, 2.360, 0.150, 1.974, 0.074};
        //for (int i = 0; i < frequency.length; i++) {
        //    countTable.add(new Node(array[i], frequency[i], null, null, false));
        //}

        //System.out.println(count.keySet());
        //System.out.println(count.values());
        return countTable;
    }
    //reorder the table to be ascending order of count(frequency)
    public static LinkedList<Node> frequencyTable(List<Node> countTable) {
        LinkedList<Node> frequencyTable = new LinkedList<Node>();

        for (Node n  : countTable) {
            if (frequencyTable.isEmpty()) { //first element
                frequencyTable.add(n);
            } else {
                int freqLarger = 0;
                for (Node i : frequencyTable) {
                    if (i.frequency > n.frequency) { //all elements with greater frequency are to the right of e
                        freqLarger++;
                    }
                }
                int index = frequencyTable.size() - freqLarger;
                frequencyTable.add(index, n); //inserts a element at the index and shifts all elements with larger frequencies to the right
            }
        }
        //System.out.println();
        //for (Node n : frequencyTable) {
        //    System.out.print(n.character + " ");
        //}
        //System.out.println();
        return frequencyTable;
    }
    //Create Huffman Tree
    public static Node huffman(LinkedList<Node> table) {
        while (table.size() > 1) {
            Node e0 = table.get(0);
            Node e1 = table.get(1);
            double frequency = e0.frequency + e1.frequency; //parent frequency = sum of children frequencies
            Node e = new Node('\0', frequency, e0, e1, false); //parent char = null
            table.remove(1);//bug fixed
            table.remove(0);
            int freqLarger = 0; //counts number of elements with larger frequencies
            for (Node i : table) {
                if (i.frequency > frequency) { //all elements with greater frequency will get shifted to the right of 'e'
                    freqLarger++;
                }
            }
            int index = table.size() - freqLarger;
            table.add(index, e);
        }
        return table.get(0);
    }
    //Generate a table with each character and its binary code
    public static HashMap<Character, String> binaryTable(Node root, List<Node> uniqueChars) {
        HashMap<Character, String> binaryTable = new HashMap<Character, String>();

        int numLeaf = 0;

        Node current = root;

        Stack<Node> path = new Stack<Node>();
        path.add(current);

        Stack<Character> binary = new Stack<Character>();

        while (numLeaf < uniqueChars.size()) {
            if (!current.isLeaf()) {
                if (current.left != null && !(current.left.visited)) { //climb down the tree to the left
                    binary.push('0');
                    current = current.left;
                    path.push(current);
                } else if (current.right != null && !(current.right.visited)) { //climb down the tree to the right
                    binary.push('1');
                    current = current.right;
                    path.push(current);
                } else { //backtrack to previous node
                    current.visited = true; //mark node as visited; both children are visited
                    path.pop();
                    binary.pop();
                    current = path.peek();
                }
            } else { //record char and binary code then backtrack
                numLeaf++; //increments # of nodes seen
                current.visited = true; //mark node as visited; it's a leaf
                String code = binary.toString();
                //System.out.println(code + " " + current.character);
                binaryTable.put(current.character, code);
                path.pop();
                binary.pop();
                current = path.peek();
            }
        }
        //System.out.println(binaryTable.keySet());
        //System.out.println(binaryTable.values());
        return binaryTable;
    }
    //Given binaryTable and the input text, encode binary code of the text
    public static String encode(HashMap<Character, String> binaryTable, char[] chars) {
        String code = "";
        for (char c : chars) {
            code = code + binaryTable.get(c);
        }
        return code;
    }
    //Given a Huffman Tree and binary code, decode the message
    public static String decode(Node root, String input) {
        String text = "";
        char[] binary = input.toCharArray();
        Node current = root;
        for (char c : binary) {
            if (current.isLeaf()) {
                text = text + current.character;
                current = root;
            }
            if (c == '0') {
                current = current.left;
            } else if (c == '1') {
                current = current.right;
            } else {
                System.out.println("------Error------WRONG INPUT DETECTED");
            }
        }
        return text;
    }
}
