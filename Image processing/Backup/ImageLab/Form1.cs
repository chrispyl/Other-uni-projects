using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Drawing.Imaging;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace ImageLab
{
    public partial class Form1 : Form
    {

        Bitmap image;
        
        public Form1()
        {
            InitializeComponent();
        }

        private void openToolStripMenuItem_Click(object sender, EventArgs e)
        {
            openFileDialog1.ShowDialog();
            image = new Bitmap(openFileDialog1.FileName);
            pictureBox1.Image = image;
        }

        private void gDIToolStripMenuItem_Click(object sender, EventArgs e)
        {
            Color original_color, target_color;
            for (int y = 0; y < image.Height; y++)
            {   for (int x = 0; x < image.Width; x++)
                {
                    original_color = image.GetPixel(x, y);
                    target_color = Color.FromArgb(255 - (int)original_color.R, 255 - (int)original_color.G, 255 - (int)original_color.B);
                    image.SetPixel(x, y, target_color);
                }
            }
            pictureBox1.Image = image;            
        }

        private void pointersToolStripMenuItem_Click(object sender, EventArgs e)
        {
            BitmapData bmData = image.LockBits(new Rectangle(0, 0, image.Width, image.Height),
            ImageLockMode.ReadWrite, PixelFormat.Format24bppRgb);
            int width = image.Width, height = image.Height;
            int stride = bmData.Stride;
            System.IntPtr Scan0 = bmData.Scan0;
            unsafe
            {
                byte* p = (byte*)(void*)Scan0;                              
                for (int y = 0; y < height+3; y++)
                {
                    for (int x = 0; x < width; x++)
                    {
                        p[y * stride + x * 3] = (byte)(255 - p[y * stride + x * 3]);
                        p[y * stride + x * 3 + 1] = (byte)(255 - p[y * stride + x * 3 + 1]);
                        p[y * stride + x * 3 + 2] = (byte)(255 - p[y * stride + x * 3 + 2]); 
                    }
                }
            }
            image.UnlockBits(bmData);
            pictureBox1.Refresh();
        }
                
        private void rToolStripMenuItem_Click(object sender, EventArgs e)
        {
            ColorComponent(image, 2);
            pictureBox1.Refresh();
        }

       

        private void gToolStripMenuItem_Click(object sender, EventArgs e)
        {

            ColorComponent(image, 1);
            pictureBox1.Refresh();
        }

        private void bToolStripMenuItem_Click(object sender, EventArgs e)
        {
            ColorComponent(image, 0);
            pictureBox1.Refresh();
        }

        private void ColorComponent(Bitmap bmp, int component)
        {
            BitmapData bmData = bmp.LockBits(new Rectangle(0, 0, bmp.Width, bmp.Height),
            ImageLockMode.ReadWrite, PixelFormat.Format24bppRgb);
            int width = bmp.Width, height = bmp.Height;
            int stride = bmData.Stride;
            System.IntPtr Scan0 = bmData.Scan0;
            unsafe
            {
                byte* p = (byte*)(void*)Scan0;
                for (int y = 0; y < height; y++)
                {
                    for (int x = 0; x < width; x++)
                    {
                        p[y * stride + x * 3] = p[y * stride + x * 3 + component];
                        p[y * stride + x * 3 + 1] = p[y * stride + x * 3 + component];
                        p[y * stride + x * 3 + 2] = p[y * stride + x * 3 + component];
                    }
                }
            }
            bmp.UnlockBits(bmData);
        }

        private void invertToolStripMenuItem_Click(object sender, EventArgs e)
        {

        }
    }
}
