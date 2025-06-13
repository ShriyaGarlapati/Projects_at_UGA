**Convolutional Neural Network (CNN) Classifier – CIFAR-10**

A PyTorch-based image classification pipeline with model performance analysis

**Overview**
This project implements a deep Convolutional Neural Network (CNN) using PyTorch to classify images from the CIFAR-10 dataset. The goal was to design a custom CNN architecture that achieves ≥75% test accuracy, visualize its inner workings, and benchmark it against a ResNet-20 model.

It was developed as part of the graduate course CSCI 8000: Special Topics in Efficient Deep Learning at the University of Georgia.

**Key Features**

Custom CNN with:

1. Conv2D, BatchNorm2D, MaxPooling, Dropout, and FC layers
2. Softmax classification head
3. Trainable with reproducible random seeds
4. Visualization of first convolutional layer outputs (feature maps)
5. Accuracy tracking and plotting (Train vs Test)
6. ResNet-20 evaluation for benchmarking
7. Computation cost analysis (MACs and parameters) using THOP
8. Inference time profiling using dummy and real input

**Technologies Used**

* PyTorch
* Torchvision
* Matplotlib – for accuracy plotting
* THOP – to compute MACs and parameter counts
* OpenCV – for image processing
* NumPy

**Files**

* CNNclassify.py - Main script for training, testing, inference<br>
* resnet20_cifar.py - ResNet-20 definition for CIFAR-10<br>
* model - Directory to save trained models<br>
* HORSE.png - Custom input image for testing<br>
* CONV_rslt.png - Output of feature map visualization<br>
* accuracy_plot.png - Accuracy vs Epochs (train/test)<br>
* README.md - Project documentation<br>


**Prerequisites**

Install Python 3.11.9 (recommended using pyenv)<br>
If you already have Python 3.11.9, you can skip this.<br>

1. Clone the Repository:<br>

git clone https://github.com/ShriyaGarlapati/Projects_at_UGA/tree/main/CNN-Classifier-main<br>
cd CNN-Classifier<br>

2. macOS Users Only – Fix for lzma module error:<br>
brew install xz<br>

If you still see ModuleNotFoundError: No module named '_lzma', run:<br>

pyenv uninstall 3.11.9<br>
export LDFLAGS="-L/opt/homebrew/opt/xz/lib"<br>
export CPPFLAGS="-I/opt/homebrew/opt/xz/include"<br>
pyenv install 3.11.9<br>

3. Install Required Python Packages:<br>

pip install torch torchvision opencv-python matplotlib thop numpy sympy<br> networkx jinja2 filelock fsspec <br>



**Usage**

Run the script with:<br>
python CNNclassify.py <command> [image_path]<br>

Available Commands<br>

1. train<br>

Trains a custom CNN on CIFAR-10 with three different seeds (189, 173, 200)<br>
Applies data augmentation (random crop, horizontal flip)<br>
Saves model to ./model/cnn_model.pth<br>
Displays a training vs. test accuracy plot<br>
Command: python CNNclassify.py train<br>

2. test <image_path><br>

Loads the trained CNN from ./model/cnn_model.pth<br>
Predicts class of the input image (resized to 32×32)<br>
Visualizes first convolutional layer filters<br>
Saves the feature map as CONV_rslt.png<br>
Command: python CNNclassify.py test HORSE.png<br>

3. resnet20<br>

Loads and evaluates a pretrained ResNet-20 model on CIFAR-10<br>
Prints test accuracy<br>
Command: python CNNclassify.py resnet20<br>