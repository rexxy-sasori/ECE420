import numpy as np

'''
upsample signal x by a factor of rate
@para
x ndarray input signal
rate integer
'''


def upsampling(x, rate):
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


def downsampling(x, rate):
    size = len(x)

    row = [0 for i in range(size)]
    downsample_matrix = np.array([row for i in range(size / rate)])

    for i in range(len(downsample_matrix)):
        for j in range(len(downsample_matrix[0])):
            return

    output = np.dot(downsample_matrix, x)

    return output


def fftmy(x, pad):
    return x


def shuffle(x):
    return x


def fast_convol(data, filter):
    return a


def block_add(data, filter, block):
    return a
