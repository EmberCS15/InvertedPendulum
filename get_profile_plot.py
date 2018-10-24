import matplotlib.pyplot as plt
import numpy as np

if __name__ == '__main__':
    profile_coords_file = open('profile_coords.txt', 'r')
    x,y = [], []
    for l in profile_coords_file:
        row = l.split()
        x.append(row[0])
        y.append(row[1])
    plt.plot(x[3:7], y[3:7])
    plt.plot(x[:3], y[:3])
    plt.plot(x[7:11], y[7:11])
    plt.xlabel('x - axis')
    # naming the y axis
    plt.ylabel('y - axis')

    # giving a title to my graph
    plt.title('My first graph!')
    plt.show()