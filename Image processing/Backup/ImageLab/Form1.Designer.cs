namespace ImageLab
{
    partial class Form1
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.pictureBox1 = new System.Windows.Forms.PictureBox();
            this.menuStrip1 = new System.Windows.Forms.MenuStrip();
            this.fileToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.FileOpen = new System.Windows.Forms.ToolStripMenuItem();
            this.processingToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.invertToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.gDIToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.pointersToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.rGBComponentsToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.rToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.gToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.bToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.openFileDialog1 = new System.Windows.Forms.OpenFileDialog();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).BeginInit();
            this.menuStrip1.SuspendLayout();
            this.SuspendLayout();
            // 
            // pictureBox1
            // 
            this.pictureBox1.Location = new System.Drawing.Point(67, 53);
            this.pictureBox1.Name = "pictureBox1";
            this.pictureBox1.Size = new System.Drawing.Size(640, 480);
            this.pictureBox1.SizeMode = System.Windows.Forms.PictureBoxSizeMode.Zoom;
            this.pictureBox1.TabIndex = 0;
            this.pictureBox1.TabStop = false;
            // 
            // menuStrip1
            // 
            this.menuStrip1.BackColor = System.Drawing.SystemColors.ButtonHighlight;
            this.menuStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.fileToolStripMenuItem,
            this.processingToolStripMenuItem});
            this.menuStrip1.Location = new System.Drawing.Point(0, 0);
            this.menuStrip1.Name = "menuStrip1";
            this.menuStrip1.Size = new System.Drawing.Size(855, 24);
            this.menuStrip1.TabIndex = 1;
            this.menuStrip1.Text = "menuStrip1";
            // 
            // fileToolStripMenuItem
            // 
            this.fileToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.FileOpen});
            this.fileToolStripMenuItem.Name = "fileToolStripMenuItem";
            this.fileToolStripMenuItem.Size = new System.Drawing.Size(37, 20);
            this.fileToolStripMenuItem.Text = "File";
            // 
            // FileOpen
            // 
            this.FileOpen.Name = "FileOpen";
            this.FileOpen.Size = new System.Drawing.Size(103, 22);
            this.FileOpen.Text = "Open";
            this.FileOpen.Click += new System.EventHandler(this.openToolStripMenuItem_Click);
            // 
            // processingToolStripMenuItem
            // 
            this.processingToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.invertToolStripMenuItem,
            this.rGBComponentsToolStripMenuItem});
            this.processingToolStripMenuItem.Name = "processingToolStripMenuItem";
            this.processingToolStripMenuItem.Size = new System.Drawing.Size(76, 20);
            this.processingToolStripMenuItem.Text = "Processing";
            // 
            // invertToolStripMenuItem
            // 
            this.invertToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.gDIToolStripMenuItem,
            this.pointersToolStripMenuItem});
            this.invertToolStripMenuItem.Name = "invertToolStripMenuItem";
            this.invertToolStripMenuItem.Size = new System.Drawing.Size(168, 22);
            this.invertToolStripMenuItem.Text = "Invert";
            this.invertToolStripMenuItem.Click += new System.EventHandler(this.invertToolStripMenuItem_Click);
            // 
            // gDIToolStripMenuItem
            // 
            this.gDIToolStripMenuItem.Name = "gDIToolStripMenuItem";
            this.gDIToolStripMenuItem.Size = new System.Drawing.Size(117, 22);
            this.gDIToolStripMenuItem.Text = "GDI+";
            this.gDIToolStripMenuItem.Click += new System.EventHandler(this.gDIToolStripMenuItem_Click);
            // 
            // pointersToolStripMenuItem
            // 
            this.pointersToolStripMenuItem.Name = "pointersToolStripMenuItem";
            this.pointersToolStripMenuItem.Size = new System.Drawing.Size(117, 22);
            this.pointersToolStripMenuItem.Text = "Pointers";
            this.pointersToolStripMenuItem.Click += new System.EventHandler(this.pointersToolStripMenuItem_Click);
            // 
            // rGBComponentsToolStripMenuItem
            // 
            this.rGBComponentsToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.rToolStripMenuItem,
            this.gToolStripMenuItem,
            this.bToolStripMenuItem});
            this.rGBComponentsToolStripMenuItem.Name = "rGBComponentsToolStripMenuItem";
            this.rGBComponentsToolStripMenuItem.Size = new System.Drawing.Size(168, 22);
            this.rGBComponentsToolStripMenuItem.Text = "RGB Components";
            // 
            // rToolStripMenuItem
            // 
            this.rToolStripMenuItem.Name = "rToolStripMenuItem";
            this.rToolStripMenuItem.Size = new System.Drawing.Size(82, 22);
            this.rToolStripMenuItem.Text = "R";
            this.rToolStripMenuItem.Click += new System.EventHandler(this.rToolStripMenuItem_Click);
            // 
            // gToolStripMenuItem
            // 
            this.gToolStripMenuItem.Name = "gToolStripMenuItem";
            this.gToolStripMenuItem.Size = new System.Drawing.Size(82, 22);
            this.gToolStripMenuItem.Text = "G";
            this.gToolStripMenuItem.Click += new System.EventHandler(this.gToolStripMenuItem_Click);
            // 
            // bToolStripMenuItem
            // 
            this.bToolStripMenuItem.Name = "bToolStripMenuItem";
            this.bToolStripMenuItem.Size = new System.Drawing.Size(82, 22);
            this.bToolStripMenuItem.Text = "B";
            this.bToolStripMenuItem.Click += new System.EventHandler(this.bToolStripMenuItem_Click);
            // 
            // openFileDialog1
            // 
            this.openFileDialog1.FileName = "openFileDialog1";
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.AutoScroll = true;
            this.BackColor = System.Drawing.SystemColors.ActiveBorder;
            this.ClientSize = new System.Drawing.Size(872, 408);
            this.Controls.Add(this.pictureBox1);
            this.Controls.Add(this.menuStrip1);
            this.MainMenuStrip = this.menuStrip1;
            this.Name = "Form1";
            this.Text = "Form1";
            this.WindowState = System.Windows.Forms.FormWindowState.Maximized;
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).EndInit();
            this.menuStrip1.ResumeLayout(false);
            this.menuStrip1.PerformLayout();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.PictureBox pictureBox1;
        private System.Windows.Forms.MenuStrip menuStrip1;
        private System.Windows.Forms.ToolStripMenuItem fileToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem FileOpen;
        private System.Windows.Forms.OpenFileDialog openFileDialog1;
        private System.Windows.Forms.ToolStripMenuItem processingToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem invertToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem gDIToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem pointersToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem rGBComponentsToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem rToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem gToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem bToolStripMenuItem;
    }
}

