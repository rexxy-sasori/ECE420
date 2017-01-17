import numpy as np 
import matplotlib.pyplot as plt 
from scipy import signal 

F_s = 48000
t = [i / F_s for i in range(2 * F_s)]

test_data = signal.chirp(t, 0, t[-1], 24000, method='logarithmic')

# ... filter ...
