import matplotlib.pyplot as plt
import numpy as np

if __name__ == '__main__':
    file_name = "profile_coords_"
    profile_names = ['angle', 'angular_velocity', 'current']
    for i in range(1,4):
        file_name_new = file_name + str(i) +'.txt'
        profile_coords_file = open(file_name_new, 'r')
        x,y = [], []
        for l in profile_coords_file:
            row = l.split(" ")
            x.append(float(row[0]))
            y.append(float(row[1]))

        x_1, y_1 = (list(t) for t in zip(*sorted(zip(x[3:7], y[3:7]))))
        x_2, y_2 = (list(t) for t in zip(*sorted(zip(x[:3], y[:3]))))
        x_3, y_3 = (list(t) for t in zip(*sorted(zip(x[7:11], y[7:11]))))
        plt.plot(x_1, y_1)
        plt.plot(x_2, y_2)
        plt.plot(x_3, y_3)
        plt.xlabel('x - axis')
        # naming the y axis
        plt.ylabel('y - axis')

        # giving a title to my graph
        plt.title(profile_names[i-1])
        plt.show()

# pyplot for profiles