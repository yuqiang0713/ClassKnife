package com.yuqiang.sampling;


import com.yuqiang.aop.annotations.Ignore;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Date : 2019/8/5
 * Time : 11:19 AM
 *
 * @author : yuqiang
 */
@Ignore
public class Node {
    String methodName;
    long timeConsuming;
    String label;

    List<Node> childs;
    Node parent;

    public Node(String name, long time) {
        this.methodName = name;
        this.timeConsuming = time;
        childs = new ArrayList<>();
    }


    private static int deepth = 0;

    public static void analysis(List<TimingData> datas, PrintWriter writer) {
        if (datas == null || datas.size() == 0) {
            return;
        }
        Node root = new Node("root", 0L);
        Node currentNode = root;
        for (TimingData data : datas) {
            if (data.isMethodIn) {
                Node node = new Node(data.methodName, data.timeStamp);
                currentNode.childs.add(node);
                node.parent = currentNode;
                currentNode = node;
            } else {
                currentNode.timeConsuming = data.timeStamp - currentNode.timeConsuming;
                currentNode.label = data.timeStamp + " <after:before> " + currentNode.timeConsuming + "....dx....." + currentNode.timeConsuming;
                currentNode = currentNode.parent;
            }
        }


        traverse(root.childs.get(0), writer);
    }

    private static void traverse(Node root, PrintWriter writer) {
        print(root, writer);
        if (root.childs == null || root.childs.size() == 0) {
            return;
        }
        deepth++;
        for (Node child : root.childs) {
            traverse(child, writer);
        }
        deepth--;
    }

    private static void print(Node node, PrintWriter writer) {
        for (int i = 0; i < deepth; i++) {
            writer.append("   ");
        }
        writer.append(node.methodName);
        writer.append(" ");
        writer.append(node.label + "  ");
        writer.append(node.timeConsuming + "\n");
    }

}
