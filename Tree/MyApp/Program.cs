using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BinaryTreeDemo
{
    public class Program
    {
        static void Main(string[] args)
        {
            BinaryTree tree = new BinaryTree();

            while (true)
            {
                Console.WriteLine("\n--- MENU ---");
                Console.WriteLine("1. Them phan tu");
                Console.WriteLine("2. Duyet cây (InOrder)");
                Console.WriteLine("3. Duyet cây (PreOrder)");
                Console.WriteLine("4. Duyet cây (PostOrder)");
                Console.WriteLine("5. Chieu cao cây");
                Console.WriteLine("6. Chieu rong cây");
                Console.WriteLine("7. Bac cua cây");
                Console.WriteLine("8. So nut la");
                Console.WriteLine("9. So nut trung gian");
                Console.WriteLine("10. Bac cua mot nut");
                Console.WriteLine("11. In cac nut theo tung muc");
                Console.WriteLine("0. Thoat");
                Console.Write("Chon: ");
                string choice = Console.ReadLine();

                switch (choice)
                {
                    case "1":
                        Console.Write("Nhap gia tri: ");
                        int val = int.Parse(Console.ReadLine());
                        tree.Insert(val);
                        break;
                    case "2":
                        Console.Write("InOrder: ");
                        tree.InOrder(tree.Root);
                        Console.WriteLine();
                        break;
                    case "3":
                        Console.Write("PreOrder: ");
                        tree.PreOrder(tree.Root);
                        Console.WriteLine();
                        break;
                    case "4":
                        Console.Write("PostOrder: ");
                        tree.PostOrder(tree.Root);
                        Console.WriteLine();
                        break;
                    case "5":
                        Console.WriteLine("Chieu cao cây: " + tree.GetHeight(tree.Root));
                        break;
                    case "6":
                        Console.WriteLine("Chieu rong cây: " + tree.GetWidth(tree.Root));
                        break;
                    case "7":
                        Console.WriteLine("Bac cua cây: " + tree.GetTreeDegree(tree.Root));
                        break;
                    case "8":
                        Console.WriteLine("So nut la: " + tree.CountLeafNodes(tree.Root));
                        break;
                    case "9":
                        Console.WriteLine("So nut trung gian: " + tree.CountIntermediateNodes(tree.Root));
                        break;
                    case "10":
                        Console.Write("Nhap gia tri nut can kiem tra: ");
                        int nodeVal = int.Parse(Console.ReadLine());
                        Node target = tree.FindNode(tree.Root, nodeVal);
                        if (target != null)
                            Console.WriteLine("Bac cua nut " + nodeVal + ": " + tree.GetNodeDegree(target));
                        else
                            Console.WriteLine("Khong tim thay nut.");
                        break;
                    case "11":
                        tree.PrintNodesByLevel(tree.Root);
                        break;
                    case "0":
                        return;
                    default:
                        Console.WriteLine("Lua chon không hop le.");
                        break;
                }
            }
        }
    }
}
