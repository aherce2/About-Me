# **AlexNet vs ViT-H: Optimizing CNNs for GPU Efficiency**

This project was completed as part of **ECE 479 \- IoT and Cognitive Computing**, where our team explored the performance trade-offs between a classic convolutional neural network (AlexNet) and a modern transformer-based model (ViT-H) on the **CIFAR-10** dataset using GPU acceleration.

Our main goal was to investigate whether an older architecture, when optimized, could achieve inference efficiency and accuracy comparable to newer, more complex models.

**Sources**: [https://paperswithcode.com/sota/image-classification-on-cifar-10](https://paperswithcode.com/sota/image-classification-on-cifar-10)

## **Project Goals**

We set out to implement and optimize the **AlexNet** model for efficient inference on GPUs using frameworks such as **TensorFlow** and **PyTorch**. While AlexNet is historically known for its success in image classification, our focus was on:

* Achieving comparable **accuracy** to the original AlexNet model on the **ImageNet** and CIFAR-10 datasets.

* Significantly **reducing inference latency** through various optimization techniques.

## **Optimization Techniques Explored**

To enhance inference performance, we experimented with:

* **Model Parallelism**

* **Model Quantization**

* **Kernel Fusion**

These techniques were applied to investigate how much efficiency could be gained without a significant drop in classification accuracy.

## **Comparison with ViT-H**

We compared our optimized AlexNet implementation with **ViT-H (Vision Transformer \- Huge)**, a modern state-of-the-art model. Our goal was to determine:

* Whether a well-optimized CNN can **compete with** or even outperform a transformer model in **latency**.

* The **trade-offs** between model complexity, accuracy, and real-time performance on constrained hardware.

## **Included in This Repository**

* 📝 **Checkpoint Presentation** – Outlines our mid-project progress and early results.

* 📄 **Final Report** – Includes detailed methodology, optimization results, challenges, and final conclusions.

* 📁 **Source Code and Notebooks** – Contains model implementations, training scripts, and benchmarking results.

 

---

