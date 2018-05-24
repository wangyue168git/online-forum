package com.bolo.test.test02;

import com.sun.org.apache.bcel.internal.generic.NEW;

import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

/**
 * @Author wangyue
 * @Date 13:40
 */
public class MathTesr {

    /*在一个二维数组中，每一行都按照从左到右递增的顺序排序，
    每一列都按照从上到下递增的顺序排序。请完成一个函数，输入这样的一个二维数组和一个整数，判断数组中是否含有该整数。

    思路： 依次比较右上角的数字；如果该数字大于要查找的数字，则剔除列；如果该数字大于要查找的数字，则剔除行；
    复杂度：O(m+n), 行数m，列数n
     */
    public boolean find(int target, int[][] array){

        boolean found = false;
        if(array == null){
            return found;
        }

        int rows,columns,row,column;
        rows = array.length;
        columns = array[0].length;
        row = 0;
        column = columns - 1;

        while(row < rows && column >= 0){
            if(array[row][column] == target){
                found = true;
                break;
            }
            else if(array[row][column] > target){
                column--;
            }
            else {
                row ++;
            }

        }
        return found;
    }


    /*
    将一个字符串中的空格替换成“%20”。
    例如，当字符串为We Are Happy.则经过替换之后的字符串为We%20Are%20Happy。
    思路：从后往前复制，数组长度会增加，或使用StringBuilder、StringBuffer类
     */
    public String replaceSpace(StringBuffer str){
        if (str == null)
            return null;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < str.length(); i++){
            if(String.valueOf(str.charAt(i)).equals(" ")){
                sb.append("%20");
            }else {
                sb.append(str.charAt(i));
            }
        }

        return String.valueOf(sb);
    }

    static class TreeNode{
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int x){
            val = x;
        }
    }

    /*
    输入某二叉树的前序遍历和中序遍历的结果，请重建出该二叉树。假设输入的前序遍历和中序遍历的结果中都不含重复的数字。
    例如输入前序遍历序列{1,2,4,7,3,5,6,8}和中序遍历序列{4,7,2,1,5,3,8,6}，则重建二叉树并返回。
    思路：先找出根节点，然后利用递归方法构造二叉树
     */
    public TreeNode reConstructBinaryTree(int[] pre,int[] in){
        if(pre == null || in == null){
            return null;
        }
        if(pre.length == 0 || in.length == 0){
            return null;
        }
        if(pre.length != in.length){
            return null;
        }

        TreeNode root = new TreeNode(pre[0]);

        for (int i = 0; i < pre.length; i++){
            if(pre[0] == in[i]){
                root.left = reConstructBinaryTree(
                        Arrays.copyOfRange(pre,1,i+1),Arrays.copyOfRange(in,0,i));
                root.right = reConstructBinaryTree(
                        Arrays.copyOfRange(pre,i+1,pre.length),Arrays.copyOfRange(in,i+1,in.length));
            }
        }

        return root;

    }

    /*
    用两个栈来实现一个队列，完成队列的Push和Pop操作。 队列中的元素为int类型。
     */
    public Stack<Integer> stack1 = new Stack<Integer>();
    public Stack<Integer> stack2 = new Stack<Integer>();
    public void push(int node){
        stack1.push(node);
    }
    public int pop()throws Exception{
        if (stack1.isEmpty() && stack2.isEmpty()){
            throw new Exception("栈为空！");
        }
        if(stack2.isEmpty()){
            while(!stack1.isEmpty()){
                stack2.push(stack1.pop());
            }
        }
        return stack2.pop();
    }


    /*
    把一个数组最开始的若干个元素搬到数组的末尾，我们称之为数组的旋转。
    输入一个非递减排序的数组的一个旋转，输出旋转数组的最小元素。
    例如数组{3,4,5,1,2}为{1,2,3,4,5}的一个旋转，该数组的最小值为1。
    NOTE：给出的所有元素都大于0，若数组大小为0，请返回0
    */
    public int minNumberInRotateArray(int[] array){
        if (array == null || array.length == 0)
            return 0;
        int left = 0;
        int right = array.length - 1;
        int mid = 0;

        while(array[left] >= array[right]){
            if (right - left <= 1){
                mid = right;
                break;
            }

            mid = (left + right)/2;
            if(array[left] == array[mid] && array[mid] == array[right]){
                if (array[left+1] != array[right-1]){
                    mid = array[left+1] < array[right-1] ? left+1:right-1;
                }else {
                    left ++;
                    right --;
                }
            }else {
                if (array[left] <= array[mid]){
                    left = mid;
                }else {
                    right = mid;
                }
            }
        }
        return array[mid];
    }

    /*
    斐波那契数列
     */
    public long fibonacci(int n){
        long result = 0;
        long preOne = 1;
        long preTwo = 0;
        if (n == 0){
            return preTwo;
        }
        if (n == 1){
            return preOne;
        }
        for (int i = 2; i <=n; i++){
            result = preOne + preTwo;
            preTwo = preOne;
            preOne = result;
        }
        return result;
    }



    /*
    输入一个整数，输出该数二进制表示中1的个数。其中负数用补码表示。
    思路：a&(a-1)的结果会将a 右边的1变为0，直到a = 0，还可以先将a&1 != 0，然后右移1位，但不能计算负数的值，
     */
    public int NumberOf1(int n){
        int count = 0;
        while(n != 0){
            count++;
            n = (n-1) & n;
        }
        return count;
    }


    public ArrayList<Integer> printMatrix(int[][] matrix){
        if (matrix == null){
            return null;
        }
        ArrayList<Integer> arrayList = new ArrayList<>();
        int start = 0;
        while(matrix.length > start*2 && matrix[0].length > start*2){
            printOneCircle(matrix,start,arrayList);
            start++;
        }
        return arrayList;
    }

    public  void printOneCircle(int[][] matrix,int start,ArrayList<Integer> arrayList){
        int endX = matrix[0].length - 1 - start;
        int endY = matrix.length - 1 - start;

        for (int i = start; i <= endX; i++)
            arrayList.add(matrix[start][i]);
        if (start < endY) {
            for (int i = start + 1; i <= endY; i++)
                arrayList.add(matrix[i][endX]);
        }
        if (start < endX && start < endY) {
            for (int i = endX - 1; i >= start; i--)
                arrayList.add(matrix[endY][i]);
        }
        if (start < endX && start < endY - 1) {
            for (int i = endY - 1; i >= start + 1; i--)
                arrayList.add(matrix[i][start]);
        }
    }

    public int FindGreatestSumOfSubArray(int[] array) {
        if (array == null || array.length == 0)
            return 0;
        int cur = array[0];
        int greatest = array[0];

        for (int i = 1; i<array.length; i++){

            if (cur < 0) {
                cur = array[i];
            } else {
                cur += array[i];
            }

            if (cur > greatest){
                greatest = cur;
            }
        }

        return greatest;
    }

    /*
    计算树的深度
     */
    public int TreeDepth(TreeNode root){
        if (root == null)
            return 0;
        int left = TreeDepth(root.left);
        int right = TreeDepth(root.right);

        return left > right ? left+1 : right+1;
    }

    /*
    平衡二叉树要么是一棵空树，要么左右子树深度之差为1，平衡因子
     */
    public boolean IsBalanced_solution(TreeNode root){
        if (root == null)
            return true;

        int left = TreeDepth(root.left);
        int right = TreeDepth(root.right);

        int diff = left - right;
        if (diff >= -1 && diff <= 1)
            return true;

        return false;
    }

    /**
     * 空间换时间
     * O(1)实现max()方法。
     * 也可用链表记录最大值变化。
     */
    public class MyStack{
        Stack<Integer> stack1 = new Stack<>();
        Stack<Integer> stack2 = new Stack<>();
        public void push(int i){
            if (i >= stack2.peek()){
                stack2.push(i);
            }
            stack1.push(i);
        }
        public int pop(){
            if (stack1.peek() == stack2.peek())
                stack2.pop();
            return stack1.pop();
        }
        public int max(){
            return stack2.peek();
        }
    }


    /**
     * 插入排序
     * 将一个元素插入到已经有序的数组中，在初始时未知是否存在有序的数据，因此将元素的第一个元素看成是有序的，
     * 与有序的数组进行比较，比它大则直接放入，比它小则移动数组元素的位置，找到合适的位置插入
     * 当只有一个数时，则不需要插入了，因此需要n-1趟排序，比如10个数，需要9趟排序
     *
     * 一个for循环内嵌一个while循环实现，
     * 外层for循环控制需要排序的趟数，
     * while循环找到合适的插入位置(并且插入的位置不能小于0)
     * @param arrays
     */
    public void insertSort(int[] arrays){
        int temp;

        for (int i = 1; i < arrays.length; i++){
            temp = arrays[i];
            int j = i-1;
            while (j >= 0 && arrays[j] > temp){
                arrays[j+1] = arrays[j];
                j--;
            }
            arrays[j+1] = temp;
        }
    }








    public static void main(String[] args) throws InterruptedException {
//        TreeNode treeNode = new MathTesr().reConstructBinaryTree(new int[]{1,2,4,3},new int[]{4,2,1,3});
//        System.out.println(treeNode);



        Thread threadA = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i =0; i< 10; i++){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName() + "----threadA----" + i);
                }
            }
        });

        Thread threadB = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i =0; i< 10; i++){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName() + "----threadB----" + i);
                }
            }
        });

        //使并行的两个线程变为串行执行，阻塞作用
        threadB.start();
        threadB.join();//join（）方法使主线程进入阻塞状态，进而等待调用join方法的线程执行完毕，然后阻塞打开,wait()方法实现阻塞

        threadA.start();


    }




}
