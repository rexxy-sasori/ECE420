# ECE420 - Spring2017
# Lab6 - Part 1: Histogram Equilization

import numpy as np
from scipy import misc
import matplotlib.pyplot as plt
import copy

# Implement This Function
def histeq(pic):
    # Follow the procedures of Histogram Equalizaion
    # Modify the pixel value of pic directly
    histo,edges = np.histogram(pic,bins = np.amax(pic) - np.amin(pic))
    histo = histo.cumsum().astype('d')

    denom = len(pic) * len(pic[0]) - 1
    L = (2**16)-1
    cdfmin = np.amin(histo)
    scaled = ((histo - cdfmin)/denom * (L-1)).astype("uint16")

    pic = np.interp(pic, edges[:-1], scaled)

    return pic

# Histogram Equilization
eco_origin = misc.imread('eco.tif');
eco_histeq = copy.deepcopy(eco_origin);

# Call to histeq to perform Histogram Equilization
eco_histeq = histeq(eco_histeq);


# Show the result in two windows
fig_eco_origin = plt.figure(1);
fig_eco_origin.suptitle('Original eco.tif', fontsize=14, fontweight='bold');
plt.imshow(eco_origin,cmap='gray',vmin = 0, vmax = 65535);
fig_eco_histeq = plt.figure(2)
fig_eco_histeq.suptitle('Histrogram Equalized eco.tif', fontsize=14, fontweight='bold');
plt.imshow(eco_histeq,cmap='gray',vmin = 0, vmax = 65535);
plt.show()