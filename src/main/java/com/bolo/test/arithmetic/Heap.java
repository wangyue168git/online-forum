package com.bolo.test.arithmetic;

import java.util.ArrayList;
import java.util.Arrays;

public class Heap {



    /*
    将一个数组 构建为大根堆,初始化为自底向上构建，从最后一个非叶子节点（array.length/2 - 1）开始。
    简单描述为：自底向上构建，自上而下调整递归
    大根堆的逻辑结构为一棵完全二叉树，存储结构为数组
    堆的删除操作：删除根节点，将堆尾节点填补到根节点，再排序使之保持堆结构。
    堆的插入算法
       将一个数据元素插入到堆中，使之依然成为一个堆。
       算法描述：先将结点插入到堆的尾部，再将该结点逐层向上调整，直到依然构成一个堆，
       调整方法是看每个子树是否符合大（小）根堆的特点，不符合的话则调整叶子和根的位置。
    每次调整都产生了一个n／2 -1的无序序列
    时间复杂度为O（NlogN）

    堆排序 ：每次把堆顶元素与堆尾元素互换，然后将除尾节点之外的堆重新构建为顶堆，重复上述操作，形成有序，
            本质上是一种选择排序
            堆的存储结构为 数组！！！
     */
    public void buildHeap(int[] array){
        for (int i = array.length/2 - 1; i >= 0; i--)
            maxHeap(array,i);
    }


    /*
     调整重建堆，自顶向下遍历调整。
     */
    public void maxHeap(int[] array,int i){

        int left = 2*i + 1;
        int right = left + 1;
        int largest = 0;

        if (left < array.length && array[left] > array[i])
            largest = left;
        else
            largest = i;

        if (right < array.length && array[right] > array[i])
            largest = right;

        if (largest != i){
            int temp = array[i];
            array[i] = array[largest];
            array[largest] = temp;
            maxHeap(array,largest); //遍历调整子节点
        }
    }


    public ArrayList<Integer> GetLeastNumbers_Solution(int[] input,int k){
        ArrayList<Integer> list = new ArrayList<Integer>();
        if (input == null|| k<=0 || k > input.length)
            return list;

        int[] array = Arrays.copyOfRange(input,0,k);
        buildHeap(array);

        for (int i = k; i < input.length; i++ ){
            if (array[0] > input[i]){
                array[0] = input[i];
                maxHeap(array,0);
            }
        }

        for (int i : array)
            list.add(i);

        return list;
    }
}
