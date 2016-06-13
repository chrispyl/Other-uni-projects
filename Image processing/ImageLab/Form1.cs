using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Drawing.Imaging;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Diagnostics;

namespace ImageLab
{
    public partial class Form1 : Form
    {

        Bitmap coocImage=null, image=null;
        float[,] pMatrix;
        int grayLevels;
        bool d01,d11,d10;
        
        public Form1()
        {
            InitializeComponent();
            d01 = true; d11 = false; d10 = false;
            label11.Text = "d(0,1)";

            pictureBox1.Image = Properties.Resources.empty;
            pictureBox2.Image = Properties.Resources.empty;
            pictureBox3.Image = Properties.Resources.Cornflower_Blue;
            pictureBox4.Image = Properties.Resources.Cornflower_Blue;
            pictureBox5.Image = Properties.Resources.light_gray;
            pictureBox6.Image = Properties.Resources.light_gray;
            pictureBox7.Image = Properties.Resources.black;
            pictureBox8.Image = Properties.Resources.black;
        }

        private void openToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (openFileDialog1.ShowDialog() == DialogResult.OK)
            {
                image = new Bitmap(openFileDialog1.FileName);
                pictureBox1.Image = image;
                pictureBox2.Image = Properties.Resources.empty;
            }
        }

        private void findCharacteristics()
        {
            //to grayscale
            Bitmap img = (Bitmap)image.Clone();

            BitmapData bmData = img.LockBits(new Rectangle(0, 0, img.Width, img.Height),
            ImageLockMode.ReadWrite, PixelFormat.Format24bppRgb);
            int width = img.Width, height = img.Height;
            int stride = bmData.Stride;
            System.IntPtr Scan0 = bmData.Scan0;

            int[,] imageArray = new int[height, width]; //the grayscaled image
            pMatrix = new float[grayLevels, grayLevels];

            unsafe
            {
                byte* p = (byte*)(void*)Scan0;
                for (int i = 0; i < height; i++)
                {
                    for (int j = 0; j < width; j++)
                    {
                        imageArray[i, j] = (byte)(0.1140 * p[i * stride + j * 3 + 2] + 0.5870 * p[i * stride + j * 3 + 1] + 0.2990 * p[i * stride + j * 3]);
                    }
                }
            }
            img.UnlockBits(bmData);
            /////////////////

            int[,] normalizedArray = new int[height, width];

            float levelSize = (float)256 / grayLevels;

            for (int i = 0; i < height; i++)
            {
                for (int j = 0; j < width; j++)
                {
                    for (int k = 1; k <= grayLevels; k++)
                    {
                        if (imageArray[i, j] <= k * levelSize-1)
                        {
                            normalizedArray[i, j] = k - 1; //values of co-occurence matrix start from 0
                            break;
                        }
                    }
                }
            }

            int totalCount;
            Dictionary<int, int> dictionary = new Dictionary<int, int>();

            if (d01)
            {
                progressBar1.Minimum = 0;
                progressBar1.Maximum = width * height;
                totalCount=(width-1)*height;

                for (int k = 0, m = width * height; k < m; k++) //all pairs loop. /give line, %give collumn
                {
                    progressBar1.Value=k;
                    //cache these values
                    int row = k / width;
                    int collumn = k % width;
                    int defaultCellValue = normalizedArray[row, collumn];
                    int offsetCellValue = normalizedArray[row, collumn + 1];

                    //check if pair appears before
                    int uniqueID = (defaultCellValue<<8) + offsetCellValue;

                    if (!dictionary.ContainsKey(uniqueID))
                    {
                        dictionary.Add(uniqueID, 1);
                    }

                    pMatrix[defaultCellValue, offsetCellValue]++;

                    if ((collumn) == (width - 1) - 1 && k != 0)
                    {
                        k++; //care the sides for outer loop
                    }
                }
            }
            else if (d11)
            {
                //do d(1,1)
                progressBar1.Minimum = 0;
                progressBar1.Maximum = width * height - width;
                totalCount = (width - 1) * (height - 1);

                for (int k = 0, m = width * height - width; k < m; k++) //all pairs loop. /give line, %give collumn
                {
                    progressBar1.Value = k;

                    //cache these values
                    int row = k / width;
                    int collumn = k % width;
                    int defaultCellValue = normalizedArray[row, collumn];
                    int offsetCellValue = normalizedArray[row + 1, collumn + 1];

                    //check if pair appears before
                    int uniqueID = (defaultCellValue << 8) + offsetCellValue; 

                    if (!dictionary.ContainsKey(uniqueID))
                    {
                        dictionary.Add(uniqueID, 1);
                    }

                    pMatrix[defaultCellValue, offsetCellValue]++;

                    if ((collumn) == (width - 1) - 1 && k != 0)
                    {
                        k++; //care the sides for outer loop
                    }
                }
            }
            else
            {
                //d(1,0)
                progressBar1.Minimum = 0;
                progressBar1.Maximum = width * height-width;
                totalCount = width * (height-1);

                for (int k = 0, m = width * height-width; k < m; k++) //all pairs loop. /give line, %give collumn
                {
                    progressBar1.Value = k;
                    //cache these values
                    int row = k / width;
                    int collumn = k % width;
                    int defaultCellValue = normalizedArray[row, collumn];
                    int offsetCellValue = normalizedArray[row+1, collumn];

                    //check if pair appears before
                    int uniqueID = (defaultCellValue << 8) + offsetCellValue; 

                    if (!dictionary.ContainsKey(uniqueID))
                    {
                        dictionary.Add(uniqueID, 1);
                    }

                    pMatrix[defaultCellValue, offsetCellValue]++;
                }
            }

            float entropy = 0;
            float energy = 0;
            float contrast = 0;
            float homogeneity = 0;
            float dissimilarity = 0;
            float correlation = 0;

            float μx = 0;
            float μy = 0;
            float σxSquared = 0;
            float σySquared = 0;
            float σx;
            float σy;

            using (System.IO.TextWriter tw = new System.IO.StreamWriter("pMatrix.txt"))
            {
                for (int i = 0; i < grayLevels; i++) //make them as probabilities
                {
                    float pijHelper = 0;

                    for (int j = 0; j < grayLevels; j++)
                    {
                        tw.Write(pMatrix[i, j] + " ");
                        pMatrix[i, j] = (float)(pMatrix[i, j] / totalCount);

                        pijHelper += pMatrix[i, j];
                        if (pMatrix[i, j] != 0) entropy -= pMatrix[i, j] * (float)Math.Log(pMatrix[i, j], 2);
                        energy += pMatrix[i, j] * pMatrix[i, j];
                        contrast += (i - j) * (i - j) * pMatrix[i, j];
                        homogeneity += (float)pMatrix[i, j] / (1 + (i - j) * (i - j));
                        dissimilarity += Math.Abs(i - j) * pMatrix[i, j];
                    }
                    tw.WriteLine();
                    μx += i * pijHelper;
                }
            }

            for (int i = 0; i < grayLevels; i++)
            {
                float sumHelper = 0;
                for (int j = 0; j < grayLevels; j++)
                {
                    sumHelper += pMatrix[i, j];
                }
                σxSquared += (i - μx) * (i - μx) * sumHelper;
            }

            for (int j = 0; j < grayLevels; j++)
            {
                float pjiHelper = 0;
                for (int i = 0; i < grayLevels; i++)
                {
                    pjiHelper += pMatrix[i, j];
                }
                μy += j * pjiHelper;
            }

            for (int j = 0; j < grayLevels; j++)
            {
                float sumHelper = 0;
                for (int i = 0; i < grayLevels; i++)
                {
                    sumHelper += pMatrix[i, j];
                }
                σySquared += (j - μy) * (j - μy) * sumHelper;
            }

            σx = (float)Math.Sqrt(σxSquared);
            σy = (float)Math.Sqrt(σySquared);

            float correlationHelper = 0;
            for (int i = 0; i < grayLevels; i++)
            {
                for (int j = 0; j < grayLevels; j++)
                {
                    correlationHelper += (i - μx) * (j - μy) * pMatrix[i, j];
                }
            }
            correlation = (float)correlationHelper / (σx * σy);

            textBox1.Text = entropy.ToString();
            textBox2.Text = energy.ToString();
            textBox3.Text = contrast.ToString();
            textBox4.Text = homogeneity.ToString();
            textBox5.Text = dissimilarity.ToString();
            textBox6.Text = correlation.ToString();
        }

        private void drawCoocMatrix()
        {
            //coocImage = new Bitmap(grayLevels, grayLevels);
            coocImage = new Bitmap(pictureBox2.Width, pictureBox2.Height);

            BitmapData bmData = coocImage.LockBits(new Rectangle(0, 0, coocImage.Width, coocImage.Height),
            ImageLockMode.ReadWrite, PixelFormat.Format24bppRgb);
            int width = coocImage.Width, height = coocImage.Height;
            int stride = bmData.Stride;
            System.IntPtr Scan0 = bmData.Scan0;

            unsafe
            {
                byte* p = (byte*)(void*)Scan0;
                float scaleFactorX=(float)pictureBox2.Width/grayLevels;
                float scaleFactorY=(float)pictureBox2.Height/grayLevels;
                using (System.IO.TextWriter tw = new System.IO.StreamWriter("pixel.txt"))
                {
                    for (int i = 0; i < pictureBox2.Height; i++)
                    {
                        for (int j = 0; j < pictureBox2.Width; j++)
                        {
                            int pMatrixX = (int)Math.Floor((float)i/scaleFactorY);
                            int pMatrixY = (int)Math.Floor((float)j/scaleFactorX);

                            float max = findMax(pMatrix);

                            p[i * stride + j * 3] = (byte)(((float)pMatrix[pMatrixX, pMatrixY]/max) * 255);
                            p[i * stride + j * 3 + 1] = (byte)(((float)pMatrix[pMatrixX, pMatrixY] / max) * 255);
                            p[i * stride + j * 3 + 2] = (byte)(((float)pMatrix[pMatrixX, pMatrixY] / max) * 255);
                            tw.Write(pMatrixX + " " + pMatrixY);
                            tw.WriteLine();
                        }
                    }
                }

                //interpolate
                int maskSize = 15;

                for(int g=0; g<3; g++)
                {
                    for (int i = 0; i < pictureBox2.Height-maskSize; i++)
                    {
                        for (int j = 0; j < pictureBox2.Width-maskSize; j++)
                        {
                            float value = 0;
                            for (int k = 0; k < maskSize; k++)
                            {
                                
                                value+=p[(i+k)*stride+(j+k)*3];
                            }
                            value/=maskSize;
                            p[i * stride + j * 3] = (byte)(value);
                            p[i * stride + j * 3 + 1] = (byte)(value);
                            p[i * stride + j * 3 + 2] = (byte)(value);
                        }
                    }

                    for (int i = pictureBox2.Height-1; i > maskSize; i--)
                    {
                        for (int j = pictureBox2.Width-1; j > maskSize; j--)
                        {
                            float value = 0;
                            for (int k = 0; k < maskSize; k++)
                            {

                                value += p[(i - k) * stride + (j - k) * 3];
                            }
                            value /= maskSize;
                            p[i * stride + j * 3] = (byte)(value);
                            p[i * stride + j * 3 + 1] = (byte)(value);
                            p[i * stride + j * 3 + 2] = (byte)(value);
                        }
                    }
                }
            }
            
            coocImage.UnlockBits(bmData);
            pictureBox2.Image = coocImage;
        }

        private float findMax(float[,] array)
        {
            float max = 0;

            for (int i = 0; i < grayLevels; i++)
            {
                for (int j = 0; j < grayLevels; j++)
                {
                    if (array[i, j] > max) max = array[i, j];
                }
            }

            return max;
        }

        private void button1_Click(object sender, EventArgs e)
        {
            if (image != null)
            {
                grayLevels=Convert.ToInt32(numericUpDown1.Text);

                Cursor.Current = Cursors.WaitCursor;
                findCharacteristics();
                drawCoocMatrix();
                Cursor.Current = Cursors.Default;
                progressBar1.Value = 0;
            }
        }

        private void saveMatrixImageToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (saveFileDialog1.ShowDialog() == DialogResult.OK && coocImage!=null)
            {
                string name = saveFileDialog1.FileName;
                coocImage.Save(name+".jpg");
            }
        }

        private void Form1_Load(object sender, EventArgs e)
        {

        }

        private void pictureBox4_Click(object sender, EventArgs e)
        {
            d01 = true; d10 = false; d11 = false;
            label11.Text = "d(0,1)";
            pictureBox4.Image = Properties.Resources.Cornflower_Blue;
            pictureBox5.Image = Properties.Resources.light_gray;
            pictureBox6.Image = Properties.Resources.light_gray;
        }

        private void pictureBox5_Click(object sender, EventArgs e)
        {
            d10 = true; d01 = false; d11 = false;
            label11.Text = "d(1,0)";
            pictureBox4.Image = Properties.Resources.light_gray;
            pictureBox5.Image = Properties.Resources.Cornflower_Blue;
            pictureBox6.Image = Properties.Resources.light_gray;
        }

        private void pictureBox6_Click(object sender, EventArgs e)
        {
            d11 = true; d01 = false; d10 = false;
            label11.Text = "d(1,1)";
            pictureBox4.Image = Properties.Resources.light_gray;
            pictureBox5.Image = Properties.Resources.light_gray;
            pictureBox6.Image = Properties.Resources.Cornflower_Blue;
        }
    }
}
