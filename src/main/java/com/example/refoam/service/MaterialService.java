package com.example.refoam.service;

import com.example.refoam.domain.*;
import com.example.refoam.dto.MaterialChart;
import com.example.refoam.repository.AlertLogRepository;
import com.example.refoam.repository.MaterialRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaterialService {
    private final MaterialRepository materialRepository;
    private final AlertLogRepository alertLogRepository;

    @Transactional
    public void save(Material material){
        materialRepository.save(material);
    }
    @Transactional
    public void delete(Long id){
        materialRepository.deleteById(id);
    }

    // 퇴사자 있을 경우 때문에 수정함
    public List<Material> selectAll() {
        List<Material> materials = materialRepository.findAll();

        materials.forEach(material -> {
            if (material.getEmployee() != null) {
                String displayName = material.getEmployee().getUsername();
                if (!material.getEmployee().isActive()) {
                    displayName += " (퇴사)";
                }
                material.setMaterialDisplayName(displayName);
            } else {
                material.setMaterialDisplayName("정보 없음");
            }
        });

        return materials;
    }

    public Optional<Material> findOne(Long id){
        return materialRepository.findById(id);
    }
    public List<Material> findMaterialName(MaterialName materialName){
        return materialRepository.findAllByMaterialName(materialName);
    }

    public Map<MaterialName, Long> getMaterialQuantities(){
        //DB에서 재료를 가져옴
        List<Material> materials = materialRepository.findAll();

        //재료 이름별로 그룹화하고 총합 계산
        Map<MaterialName, Long> materialNameLongMap = materials.stream().collect(Collectors.groupingBy(Material :: getMaterialName, //재료 이름 기준으로 그룹화
                Collectors.summingLong(Material::getMaterialQuantity) // 각 그룹의 총합 계산
        ));
        return materialNameLongMap;
    }

    //제품 별 필요한 원재료 매핑
    private static final Map<ProductName, List<MaterialName>> PRODUCT_NAME_LIST_MAP = Map.of(
            ProductName.NORMAL, List.of(MaterialName.EVA, MaterialName.P_BLUE, MaterialName.P_RED, MaterialName.P_WHITE), // 원통형 연보라(파+빨+흰)\
            ProductName.BUMP, List.of(MaterialName.EVA, MaterialName.P_BLACK), // 돌기형
            ProductName.HALF, List.of(MaterialName.EVA, MaterialName.P_WHITE, MaterialName.P_RED) // 반원형 연분홍(빨+흰)
    );

    // productName에 필요한 재료의 총합 가져오기
    public Map<MaterialName, Long> getRequiredMaterialStock(ProductName productName){
        // 전체 원재료 수량 가져오기
        Map<MaterialName, Long> materialQuantities = getMaterialQuantities();

        // 제품별 필요한 원재료 리스트 가져오기
        List<MaterialName> requiredMaterials = PRODUCT_NAME_LIST_MAP.getOrDefault(productName, List.of());

        log.info("원재료 리스트{}", requiredMaterials);

        // 필요한 원재료 수량만 추출
        Map<MaterialName, Long> materialNameLongMap = requiredMaterials.stream().collect(Collectors.toMap(
                materialName -> materialName,
                materialName -> materialQuantities.getOrDefault(materialName,0L)
        ));
        return materialNameLongMap;
    }

    // 주문 수량과 비교하여 재고 체크, 주문을 넣을 때 현재 재고가 충분한지 검사
    public boolean isEnoughMaterial(ProductName productName, int orderQuantity){
        //productName에 따라 필요한 재료 수량 가져오기
        Map<MaterialName, Long> requiredMaterialStock = getRequiredMaterialStock(productName);

        //전체 원재료 수량 가져오기
        Map<MaterialName, Long> materialQuantities = getMaterialQuantities();

        //모든 원재료가 주문량을 충족하는지 확인
        return requiredMaterialStock.entrySet().stream().allMatch(entry -> materialQuantities.getOrDefault(entry.getKey(), 0L) >= orderQuantity);
    }

    // 페이징 구현용
    public Page<Material> getList(int page){
        // 최신순으로 보이게하기
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));

        PageRequest pageable = PageRequest.of(page, 12, Sort.by(sorts));
        Page<Material> pageResult = materialRepository.findAll(pageable);

        pageResult.forEach(material -> {
            if (material.getEmployee() != null) {
                String displayName = material.getEmployee().getUsername();
                if (!material.getEmployee().isActive()) {
                    displayName += " (퇴사)";
                }
                material.setMaterialDisplayName(displayName);
            } else {
                material.setMaterialDisplayName("정보 없음");
            }
        });

        return pageResult;

//        return this.materialRepository.findAll(pageable);
    }

    /*public List<MaterialChart> getMaterialChart() {
        List<Material> materials = materialRepository.findAll();

        return materials.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getMaterialDate().toLocalDate(),
                        Collectors.summingInt(Material::getMaterialQuantity)
                ))
                .entrySet().stream()
                .map(entry -> new MaterialChart(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(MaterialChart::getDate))
                .toList();
    }*/
}
