# **🎨 Smart Colorimeter – ECE 445 Senior Design Project**

This project was developed for **ECE 445 – Senior Design**, a hardware-focused course centered on designing and building custom PCBs to solve real-world problems. Our team, composed entirely of Computer Engineering majors, chose to tackle a **beauty-tech challenge**: accurately identifying skin tones to assist users—particularly makeup users—with product selection.

---

### **🛠️ Project Overview**

Our original idea aimed to develop a handheld device that could:

* Detect a user's **skin tone**

* Measure **moisture levels** on the skin

* Recommend **oil- or water-based** products accordingly

However, due to hardware limitations—such as the need to custom design an **interdigitated capacitor** for moisture sensing—we pivoted to focus entirely on **color analysis** using an **XYZ light sensor system**.

---

### **🔄 Pivot and Discovery**

We experimented with the **OPT4048 XYZ color sensor**, only to discover it’s best suited for **illuminated surfaces**, not opaque ones like skin. Realizing this, we restructured the system:

* Used a **controlled lighting setup** to standardize readings

* Displayed **accurate skin tone results on an iPad**

* Mapped the output using the **Google Monk Skin Tone Scale**

Despite the shift in direction, we were able to develop a working prototype that provided **consistent and accurate skin tone classifications** under varying lighting conditions.

---

### **💻 My Role**

While the class was hardware-heavy, my primary contributions were on the **software and embedded systems side**, including:

* Writing all **embedded C/C++ code** for sensor data acquisition (Arduino IDE)

* Developing the **algorithm for color classification**

* Designing the **UI for output display**

* Handling **database and frontend integration ideas**

* And… avoiding too much soldering 😉 (not my strength\!)

---

### **🌟 Why This Project Matters**

As a woman who’s personally struggled with finding the right **makeup shade**, this project was both **technically engaging and deeply relatable**. The beauty industry offers an overwhelming range of options, and matching the right skin tone to a product is still a widespread challenge. This project gave me a chance to **engineer a solution to a real problem I care about**.

---

### **🚀 What’s Next?**

Although this course project concluded with a working hardware prototype, I’m **continuing development independently**\! I’m currently building an **app-based version of the skin tone algorithm** that can help users match makeup shades directly from their phone or tablet—**no extra hardware needed**.

I’m excited about where this could go and hope to share updates soon\!

---

### **📁 Folder Contents**

This folder is still being organized but currently includes:

* 📄 `Project_Proposal.pdf` – Original project plan

* 📝 `Final_Report.pdf` – Detailed technical and design documentation

* 🎥 `/Demo Videos`: –  Walkthrough of our prototype in action  
   *(Note: Source code will be added soon once it's cleaned and structured.)*

---

