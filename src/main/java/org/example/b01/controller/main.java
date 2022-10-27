package org.example.b01.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class main {
        static ArrayList<Integer>[] A;
        static boolean[] visited;

        public static void main(String[] args) throws IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            StringTokenizer token = new StringTokenizer(br.readLine());

            int n = Integer.parseInt(token.nextToken());
            int m = Integer.parseInt(token.nextToken());
            A = new ArrayList[n+1];
            visited = new boolean[n+1];

            for(int i=1;i<n+1;i++){
                A[i] = new ArrayList<Integer>();
            }

            for(int i=0;i<m;i++){
                token = new StringTokenizer(br.readLine());
                int u = Integer.parseInt(token.nextToken());
                int v = Integer.parseInt(token.nextToken());

                A[u].add(v);
                A[v].add(u);
            }
            int cnt = 0;
            for(int i=1;i<n+1;i++){
                if(!visited[i]){
                    cnt++;
                    dfs(i);
                }
            }
            System.out.println(cnt);
        }
        static void dfs(int node){
            if(visited[node]){
                return;
            }
            visited[node] = true;
            for(int i:A[node]){
                if(visited[i] == false){
                    dfs(i);
                }
            }
        }
    }
