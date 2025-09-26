using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BinaryTreeDemo
{
    public class BinaryTree
    {
        public Node Root;

        // Thêm nút theo thứ tự từ trái sang phải (dùng hàng đợi)
        public void Insert(int value)
        {
            Node newNode = new Node(value);
            if (Root == null)
            {
                Root = newNode;
                return;
            }

            Queue<Node> queue = new Queue<Node>();
            queue.Enqueue(Root);

            while (queue.Count > 0)
            {
                Node current = queue.Dequeue();

                if (current.Left == null)
                {
                    current.Left = newNode;
                    break;
                }
                else
                    queue.Enqueue(current.Left);

                if (current.Right == null)
                {
                    current.Right = newNode;
                    break;
                }
                else
                    queue.Enqueue(current.Right);
            }
        }

        // Duyệt cây
        public void InOrder(Node node)
        {
            if (node == null) return;
            InOrder(node.Left);
            Console.Write(node.Data + " ");
            InOrder(node.Right);
        }

        public void PreOrder(Node node)
        {
            if (node == null) return;
            Console.Write(node.Data + " ");
            PreOrder(node.Left);
            PreOrder(node.Right);
        }

        public void PostOrder(Node node)
        {
            if (node == null) return;
            PostOrder(node.Left);
            PostOrder(node.Right);
            Console.Write(node.Data + " ");
        }

        // Chiều cao cây
        public int GetHeight(Node node)
        {
            if (node == null) return -1;
            return Math.Max(GetHeight(node.Left), GetHeight(node.Right)) + 1;
        }

        // Chiều rộng cây
        public int GetWidth(Node root)
        {
            if (root == null) return 0;

            Queue<Node> queue = new Queue<Node>();
            queue.Enqueue(root);
            int maxWidth = 0;

            while (queue.Count > 0)
            {
                int levelWidth = queue.Count;
                maxWidth = Math.Max(maxWidth, levelWidth);

                for (int i = 0; i < levelWidth; i++)
                {
                    Node current = queue.Dequeue();
                    if (current.Left != null) queue.Enqueue(current.Left);
                    if (current.Right != null) queue.Enqueue(current.Right);
                }
            }

            return maxWidth;
        }

        // Bậc của một nút
        public int GetNodeDegree(Node node)
        {
            if (node == null) return -1;
            int degree = 0;
            if (node.Left != null) degree++;
            if (node.Right != null) degree++;
            return degree;
        }

        // Bậc của cây
        public int GetTreeDegree(Node node)
        {
            if (node == null) return -1;
            int left = GetTreeDegree(node.Left);
            int right = GetTreeDegree(node.Right);
            int current = GetNodeDegree(node);
            return Math.Max(current, Math.Max(left, right));
        }

        // Số nút lá
        public int CountLeafNodes(Node node)
        {
            if (node == null) return 0;
            if (node.Left == null && node.Right == null) return 1;
            return CountLeafNodes(node.Left) + CountLeafNodes(node.Right);
        }

        // Số nút trung gian
        public int CountIntermediateNodes(Node node)
        {
            if (node == null) return 0;
            if (node.Left == null && node.Right == null) return 0;
            return 1 + CountIntermediateNodes(node.Left) + CountIntermediateNodes(node.Right);
        }

        // Tìm node theo giá trị
        public Node FindNode(Node root, int value)
        {
            if (root == null) return null;
            if (root.Data == value) return root;

            Node left = FindNode(root.Left, value);
            if (left != null) return left;

            return FindNode(root.Right, value);
        }
        public void PrintNodesByLevel(Node root)
        {
            if (root == null)
            {
                Console.WriteLine("Cây rong.");
                return;
            }

            Queue<Node> queue = new Queue<Node>();
            queue.Enqueue(root);
            int level = 0;

            while (queue.Count > 0)
            {
                int levelSize = queue.Count;
                Console.Write("Muc " + level + ": ");

                for (int i = 0; i < levelSize; i++)
                {
                    Node current = queue.Dequeue();
                    Console.Write(current.Data + " ");

                    if (current.Left != null) queue.Enqueue(current.Left);
                    if (current.Right != null) queue.Enqueue(current.Right);
                }

                Console.WriteLine();
                level++;
            }
        }

    }

}
