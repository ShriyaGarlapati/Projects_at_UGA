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
PyTorch

Torchvision

Matplotlib – for accuracy plotting

THOP – to compute MACs and parameter counts

OpenCV – for image processing

NumPy

**Project Structure**

.
├── CNNclassify.py             # Main script for training, testing, inference
├── resnet20_cifar.py          # ResNet-20 definition for CIFAR-10
├── model/                     # Directory to save trained models
│   └── cnn_model.pth
├── HORSE.png                  # Custom input image for testing
├── CONV_rslt.png              # Output of feature map visualization
├── accuracy_plot.png          # Accuracy vs Epochs (train/test)
├── README.md                  # Project documentation


**Prerequisites**

Install Python 3.11.9 (recommended using pyenv)
If you already have Python 3.11.9, you can skip this.

1. Clone the Repository:

git clone https://github.com/ShriyaGarlapati/Projects_at_UGA/tree/main/CNN-Classifier-main
cd CNN-Classifier

2. macOS Users Only – Fix for lzma module error:
brew install xz

If you still see ModuleNotFoundError: No module named '_lzma', run:

pyenv uninstall 3.11.9
export LDFLAGS="-L/opt/homebrew/opt/xz/lib"
export CPPFLAGS="-I/opt/homebrew/opt/xz/include"
pyenv install 3.11.9

3. Install Required Python Packages:

pip install torch torchvision opencv-python matplotlib thop numpy sympy networkx jinja2 filelock fsspec



**Usage**

Run the script with:
python CNNclassify.py <command> [image_path]

Available Commands

1. train

Trains a custom CNN on CIFAR-10 with three different seeds (189, 173, 200)
Applies data augmentation (random crop, horizontal flip)
Saves model to ./model/cnn_model.pth
Displays a training vs. test accuracy plot
Command: python CNNclassify.py train

2. test <image_path>

Loads the trained CNN from ./model/cnn_model.pth
Predicts class of the input image (resized to 32×32)
Visualizes first convolutional layer filters
Saves the feature map as CONV_rslt.png
Command: python CNNclassify.py test HORSE.png

3. resnet20

Loads and evaluates a pretrained ResNet-20 model on CIFAR-10
Prints test accuracy
Command: python CNNclassify.py resnet20