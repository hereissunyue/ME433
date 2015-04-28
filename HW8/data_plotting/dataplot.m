%% Preprocessing
% Path of the accdata please change this corresponding, thank you!
addpath('C:\Users\sunyue\Desktop\Advanced Mechatronic\HW8\data_ploting')

[x1,y1,z1] = textread('accdata1.txt', '%f%f%f','commentstyle', 'shell');
[x2,y2,z2] = textread('accdata2.txt', '%f%f%f','commentstyle', 'shell');
[x3,y3,z3] = textread('accdata3.txt', '%f%f%f','commentstyle', 'shell');


%% Plotting
% plot the data
figure(1)
plot(x1,'r','LineWidth',2);
hold on
plot(y1,'g','LineWidth',2);
hold on
plot(z1,'b','LineWidth',2);
hold off
legend('x','y','z');
grid on

figure(2)
plot(x2,'r','LineWidth',2);
hold on
plot(y2,'g','LineWidth',2);
hold on
plot(z2,'b','LineWidth',2);
hold off
legend('x','y','z');
grid on

figure(3)
plot(x3,'r','LineWidth',2);
hold on
plot(y3,'g','LineWidth',2);
hold on
plot(z3,'b','LineWidth',2);
hold off
legend('x','y','z');
grid on