test_data = [1, 2, 3, 4, 5, 6]
b = [1, 2, 3]


def my_convolution(data, filter):
    N = len(test_data)
    M = len(b)
    out_put = [0 for i in range(N + M - 1)]
    left = 0
    right = 0

    for i in range(len(out_put)):
        if i >= M - 1 and i <= N - 1:
            '''
            if the filter is within the data
            '''
            left = 0
            right = M

        elif i < M - 1:
            '''
            left out of range
            '''
            left = 0
            right = i + 1

        elif i > N - 1:
            '''
            right out of range
            '''
            left = i + 1 - N
            right = M
        for j in range(left, right):
            out_put[i] = out_put[i] + test_data[i - j] * b[j]

    return out_put


out = my_convolution(data=test_data, filter=b)

print(out)
from scipy import signal

print(signal.convolve(b, test_data))
