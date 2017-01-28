import numpy as np

'''
upsample signal x by a factor of rate
@para
x ndarray input signal
rate integer
'''
def upsampling(x,up_factor):
    size = len(x)

    row = [0 for i in range(size)]
    upsample_matrix = np.array([row for i in range(rate * size)])

    return x

'''
downsample signal x by a factor of rate
@para
x ndarray input signal
rate integer
'''
def downsampling(x,down_factor):
    size = len(x)

    row = [0 for i in range(size)]
    downsample_matrix = np.array([row for i in range(size / rate)])

    for i in range(len(downsample_matrix)):
        for j in range(len(downsample_matrix[0])):
            return

    output = np.dot(downsample_matrix, x)

    return output

def dft(x,pad):
    return x

def fft(x):
    N = len(x)
    if N % 2 == 1:
        return dft(x)

    else:
        twiddle_factor = 1
        even = x[0:N-2:2]
        odd = x[1:N-1:2]
        Xe = fft(x)
        Xo = fft(x)
        return [Xe,Xe] + twiddle_factor * [Xo,Xo]

'''
shuffle the input sequence so that fft can do both
DIT  and DIF algorithm to do fft
'''
def shuffle(x):
    return x

def fast_convol(data, filter):
    return a

def block_add(data, filter, block):
    return a

def ifft(x):
    return x